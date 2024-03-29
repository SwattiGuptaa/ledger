package com.hsbc.ledger.validator;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.exception.AccountClosedException;
import com.hsbc.ledger.exception.InsufficientBalanceException;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WalletValidator {
    @Autowired
    private final WalletRepository walletRepository;
    public void validateSingleTransferEvent(PostingEvent postingEvent){
        //validates if valid wallet id is provided
        Wallet fromWallet = getWallet(postingEvent.getFromWalletId(), "From wallet not found");
        Wallet toWallet = getWallet(postingEvent.getToWalletId(), "To wallet not found");

//        List<Wallet> toWalletIds = walletRepository.findByIdIn(postingEvent.getToWalletIds());
//        if(toWalletIds.size() != postingEvent.getToWalletIds().size()){
//            new WalletNotFoundException("Invalid To wallet Ids provided");
//        }

        //Validate wallets belong to same account
       // boolean isSameAccountForToWalletIds = toWalletIds.stream().map(Wallet::getAccount).distinct().count() == 1;
//        Account fromAccount = fromWallet.getAccount();
//        if(isSameAccountForToWalletIds && !fromAccount.getId().equals(toWalletIds.get(0).getId())){
//            throw new AccountClosedException("Wallets belong to different Accounts");
//        }

        Account fromAccount = fromWallet.getAccount();
        Account toAccount = toWallet.getAccount();
        if(!fromAccount.getId().equals(toAccount.getId())){
            throw new AccountClosedException("Wallets belong to different Accounts");
        }
        //Validate account is open
        if(!fromAccount.getStatus().equals(Account.AccountStatus.OPEN)){
            throw new AccountClosedException("Account is closed and hence transfer cannot be done");
        }

        // Validate if the from wallet has sufficient balance
        if (fromWallet.getBalance().compareTo(postingEvent.getTotalAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in the wallet");
        }

    }

    private Wallet getWallet(Long walletId, String errorMsg) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(errorMsg));

    }
}
