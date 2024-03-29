package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.query.WalletLedgerDTO;
import com.hsbc.ledger.entity.PostingQuery;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.entity.WalletLedger;
import com.hsbc.ledger.repository.PostingQueryRepository;
import com.hsbc.ledger.repository.WalletLedgerRepository;
import com.hsbc.ledger.repository.WalletRepository;
import com.hsbc.ledger.service.impl.LedgerQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LedgerQueryServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private PostingQueryRepository postingQueryRepository;

    @Mock
    private WalletLedgerRepository walletLedgerRepository;

    @InjectMocks
    private LedgerQueryServiceImpl ledgerQueryService;

    @Test
    void testQueryWalletLedger_ValidWalletId() {
        // Mock wallet data
        long walletId = 1L;
        String walletName = "Test Wallet";
        Wallet wallet = new Wallet(walletId, walletName);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Mock WalletLedger data
        List<WalletLedger> walletLedgerList = new ArrayList<>();
        BigDecimal balanceUntilYesterday = BigDecimal.valueOf(500);
        WalletLedger walletLedger = new WalletLedger();
        walletLedger.setBalance(balanceUntilYesterday);
        walletLedgerList.add(walletLedger);
        when(walletLedgerRepository.findByWalletIdAndReconcileDateLessThanEqual(any(Long.class), any(LocalDate.class)))
                .thenReturn(walletLedgerList);

        // Mock PostingQuery data
        List<PostingQuery> postingQueryList = new ArrayList<>();
        BigDecimal todayBalance = BigDecimal.valueOf(300);
        PostingQuery postingQuery = new PostingQuery();
        postingQuery.setAmount(todayBalance);
        postingQueryList.add(postingQuery);
        when(postingQueryRepository.findByWalletIdAndStatusAndTimestampBetween(any(Long.class), PostingQuery.PostingStatus.CLEARED, any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(postingQueryList);

        // Call the service method
        WalletLedgerDTO walletLedgerDTO = ledgerQueryService.queryWalletLedger(walletId);

        // Verify the result
        assertEquals(BigDecimal.valueOf(800), walletLedgerDTO.balance());
    }

}
