package com.hsbc.ledger.validator;

import com.hsbc.ledger.dto.command.MultiplePostingEvent;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.exception.AccountClosedException;
import com.hsbc.ledger.exception.InsufficientBalanceException;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class WalletValidator {
    @Autowired
    private final WalletRepository walletRepository;

    public void validateSinglePostingEvent(PostingEvent postingEvent){
        //validates if valid wallet id is provided
        Wallet fromWallet = getWallet(postingEvent.getFromWalletId(), "From wallet not found");
        Wallet toWallet = getWallet(postingEvent.getToWalletId(), "To wallet not found");

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

    public void validateMultipleTransferEvent(MultiplePostingEvent multiplePostingEvent){
        Set<Long> toWalletIds;

        if(multiplePostingEvent.getDestinationWallets().size() > 0){
            toWalletIds = multiplePostingEvent.getDestinationWallets().keySet();
        } else{
            throw new RuntimeException("toWalletIds not provided");
        }

        // Validate fromWallet
        Wallet fromWallet = getWallet(multiplePostingEvent.getFromWalletId(), "From wallet not found");

        List<Wallet> toWalletIdList = walletRepository.findByIdIn(toWalletIds);
        if(toWalletIdList.size() != toWalletIds.size()){
            throw new WalletNotFoundException("Invalid toWalletIds provided");
        }

        //Validate wallets belong to same account
        boolean isSameAccountForToWalletIds = toWalletIdList.stream().map(Wallet::getAccount).distinct().count() == 1;
        Account fromAccount = fromWallet.getAccount();
        if(isSameAccountForToWalletIds && !fromAccount.getId().equals(toWalletIdList.get(0).getId())){
            throw new AccountClosedException("Wallets belong to different Accounts");
        }
    }

    private Wallet getWallet(Long walletId, String errorMsg) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(errorMsg));

    }
}
