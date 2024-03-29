package com.hsbc.ledger.service.impl;

import com.hsbc.ledger.dto.query.WalletLedgerDTO;
import com.hsbc.ledger.entity.PostingQuery;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.entity.WalletLedger;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.repository.PostingQueryRepository;
import com.hsbc.ledger.repository.WalletLedgerRepository;
import com.hsbc.ledger.repository.WalletRepository;
import com.hsbc.ledger.service.LedgerQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.hsbc.ledger.entity.PostingQuery.PostingStatus.CLEARED;

@Service
@RequiredArgsConstructor
public class LedgerQueryServiceImpl implements LedgerQueryService {

    @Autowired
    private final PostingQueryRepository postingQueryRepository;

    @Autowired
    private final WalletLedgerRepository walletLedgerRepository;

    @Autowired
    private final WalletRepository walletRepository;


    public WalletLedgerDTO queryWalletLedger(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Invalid walletId provided"));

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        // Subtract one day to get yesterday's date
        LocalDate yesterdayDate = currentDate.minusDays(1);

        // Get the start of the current day (midnight)
        LocalDateTime fromTimeStamp = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        // Get the current timestamp
        LocalDateTime toTimeStamp = LocalDateTime.now();

        BigDecimal totalBalance = getBalanceOfWallet(walletId, yesterdayDate, fromTimeStamp, toTimeStamp);

        return new WalletLedgerDTO(
                walletId,
                wallet.getWalletName(),
                totalBalance);
    }

    public WalletLedgerDTO queryWalletLedgerUntilTime(Long walletId, LocalDateTime dateTime) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Invalid walletId provided"));

        //get date for day before the dateTime provided
        LocalDate date = dateTime.toLocalDate();
        LocalDate yesterdayDate = date.minusDays(1);

        // Get the start of the dae provided (midnight)
        LocalDateTime fromTimeStamp = dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

        BigDecimal totalBalance = getBalanceOfWallet(walletId, yesterdayDate, fromTimeStamp, dateTime);

        return new WalletLedgerDTO(
                walletId,
                wallet.getWalletName(),
                totalBalance);
    }

    private BigDecimal getBalanceOfWallet(Long walletId, LocalDate untilDate, LocalDateTime fromTimeStamp, LocalDateTime toTimeStamp) {

        //Fetch the balance from curated data until provided date
        List<WalletLedger> walletLedgerList = walletLedgerRepository.findByWalletIdAndReconcileDateLessThanEqual(walletId, untilDate);

        List<PostingQuery> postingQueryList = postingQueryRepository.findByWalletIdAndStatusAndTimestampBetween(walletId, CLEARED, fromTimeStamp, toTimeStamp);

        BigDecimal balanceUntilYesterday = walletLedgerList.stream()
                .map(WalletLedger::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal todayBalance = postingQueryList.stream()
                .map(PostingQuery::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return balanceUntilYesterday.add(todayBalance);
    }
}
