package com.hsbc.ledger.service.impl;

import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.exception.AccountNotFoundException;
import com.hsbc.ledger.exception.InvalidAccountStatusException;
import com.hsbc.ledger.repository.AccountRepository;
import com.hsbc.ledger.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Override
    public void updateAccountStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        // Update the account status
        try {
            account.setStatus(Account.AccountStatus.valueOf(status));
        }catch(IllegalArgumentException e){
            throw new InvalidAccountStatusException("Status provided to change to is invalid");
        }

        // Save the updated account
        accountRepository.save(account);
    }
}
