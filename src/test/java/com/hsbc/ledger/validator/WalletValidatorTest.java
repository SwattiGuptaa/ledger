package com.hsbc.ledger.validator;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.exception.AccountClosedException;
import com.hsbc.ledger.exception.InsufficientBalanceException;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.hsbc.ledger.entity.Account.AccountStatus.CLOSED;
import static com.hsbc.ledger.entity.Account.AccountStatus.OPEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletValidatorTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletValidator walletValidator;

    @Test
    void validateSinglePostingEvent_InsufficientBalance() {
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);
        postingEvent.setToWalletId(2L);
        postingEvent.setTotalAmount(BigDecimal.valueOf(100));

        Account account = new Account();
        account.setId(1L);
        account.setStatus(OPEN);

        Wallet fromWallet = new Wallet();
        fromWallet.setId(1L);
        fromWallet.setBalance(BigDecimal.valueOf(10)); // Insufficient balance
        fromWallet.setAccount(account);

        when(walletRepository.findById(anyLong())).thenReturn(java.util.Optional.of(fromWallet));

        assertThrows(InsufficientBalanceException.class, () -> walletValidator.validateSinglePostingEvent(postingEvent));
    }

    @Test
    void validateSinglePostingEvent_WalletNotFound() {
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);

        when(walletRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletValidator.validateSinglePostingEvent(postingEvent));
    }

    @Test
    void validateSinglePostingEvent_AccountClosed() {
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);
        postingEvent.setToWalletId(2L);
        postingEvent.setTotalAmount(BigDecimal.valueOf(10));

        Wallet fromWallet = new Wallet();
        fromWallet.setId(1L);
        fromWallet.setBalance(BigDecimal.valueOf(20));

        Account account = new Account();
        account.setId(1L);
        account.setStatus(CLOSED);

        fromWallet.setAccount(account);

        when(walletRepository.findById(anyLong())).thenReturn(java.util.Optional.of(fromWallet));

        assertThrows(AccountClosedException.class, () -> walletValidator.validateSinglePostingEvent(postingEvent));
    }

}
