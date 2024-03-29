package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.entity.Event;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.repository.PostingCommandRepository;
import com.hsbc.ledger.repository.PostingEventRepository;
import com.hsbc.ledger.repository.PostingQueryRepository;
import com.hsbc.ledger.repository.WalletRepository;
import com.hsbc.ledger.rmq.publish.MessagePublisher;
import com.hsbc.ledger.service.impl.LedgerCommandServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LedgerCommandServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private PostingCommandRepository postingCommandRepository;
    @Mock
    private PostingQueryRepository postingQueryRepository;
    @Mock
    private PostingEventRepository postingEventRepository;
    @Mock
    private ConversionService conversionService;
    @Mock
    private MessagePublisher messagePublisher;

    @InjectMocks
    private LedgerCommandServiceImpl ledgerCommandService;

    @Test
    @Transactional
    public void testCreatePostingCommand_WithValidData() {
        // Prepare test data
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);
        postingEvent.setToWalletId(2L);
        postingEvent.setTotalAmount(BigDecimal.valueOf(300));

        // Mock the wallet repository
        Wallet fromWallet = new Wallet();
        Wallet toWallet = new Wallet();
        when(walletRepository.findById(1L)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(toWallet));
        when(conversionService.convert(postingEvent, Event.class)).thenReturn(new Event());
        // Call the method under test
        ledgerCommandService.createPostingCommand(postingEvent);

        // Verify the behavior
        verify(walletRepository, times(2)).findById(anyLong());

    }

}
