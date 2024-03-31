package com.hsbc.ledger.service.impl;

import com.hsbc.ledger.dto.PostingEventTypeEnum;
import com.hsbc.ledger.dto.command.MultiplePostingEvent;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.dto.command.PostingResponse;
import com.hsbc.ledger.entity.Event;
import com.hsbc.ledger.entity.PostingCommand;
import com.hsbc.ledger.entity.PostingQuery;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.exception.InsufficientBalanceException;
import com.hsbc.ledger.repository.PostingCommandRepository;
import com.hsbc.ledger.repository.PostingEventRepository;
import com.hsbc.ledger.repository.PostingQueryRepository;
import com.hsbc.ledger.repository.WalletRepository;
import com.hsbc.ledger.rmq.publish.MessagePublisher;
import com.hsbc.ledger.service.LedgerCommandService;
import com.hsbc.ledger.validator.WalletValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerCommandServiceImpl implements LedgerCommandService {

    private static final String POSTING_EVENT_ACCEPTED_MSG = "Posting event Accepted";
    @Autowired
    private final WalletRepository walletRepository;
    @Autowired
    private final PostingCommandRepository postingCommandRepository;
    @Autowired
    private final PostingQueryRepository postingQueryRepository;
    @Autowired
    private final PostingEventRepository postingEventRepository;
    @Autowired
    private final WalletValidator walletValidator;
    @Autowired
    private final MessagePublisher messagePublisher;
    private final ConversionService conversionService;
    private Logger logger = LoggerFactory.getLogger(LedgerCommandServiceImpl.class);

    /**
     * This method validate Posting_event, convert it to Create event and publish it to MQ
     * @param postingEvent validate this and send it over MQ
     * @return PostingResponse that will confirm client that request has been accepted
     */
    public PostingResponse validateAndSendPostingEvent(PostingEvent postingEvent) {
        logger.debug("validate the requested PostingEvent");
        walletValidator.validateSinglePostingEvent(postingEvent);

        //convert to Event and store in eventStore
        Event event = conversionService.convert(postingEvent, Event.class);
        event.setEventType(PostingEventTypeEnum.CREATE_POSTING_EVENT.getEventName());
        postingEventRepository.save(event);

        //publish the create posting event -> which will have posting command status as pending
        // TODO use Transaction Outbox pattern
        postingEvent.setEventType(PostingEventTypeEnum.CREATE_POSTING_EVENT.getEventName());
        postingEvent.setCorrelationId(event.getCorrelationId());
        messagePublisher.sendMessage(postingEvent);

        PostingResponse response = new PostingResponse(LocalDateTime.now(), POSTING_EVENT_ACCEPTED_MSG);
        // TODO provide a URL in response to check if posting cleared or failed

        return response;
    }

    /**
     * This method accepts CreatePostingEvent and creates PostingCommand with status PENDING
     * and publish event that it is in_process
     *
     * @param postingEvent - Posting event that should be processed
     */
    @Transactional
    public void createPostingCommand(PostingEvent postingEvent) {
        Wallet fromWallet = walletRepository.findById(postingEvent.getFromWalletId()).get();
        Wallet toWallet = walletRepository.findById(postingEvent.getToWalletId()).get();

        String correlationId = postingEvent.getCorrelationId();

        savePostingCommands(postingEvent, fromWallet, toWallet, correlationId, PostingCommand.PostingStatus.PENDING);
        savePostingQueries(postingEvent, fromWallet, toWallet, correlationId, PostingQuery.PostingStatus.PENDING);

        //convert to Event and store in eventStore
        Event event = conversionService.convert(postingEvent, Event.class);
        event.setEventType(PostingEventTypeEnum.IN_PROCESS_POSTING_EVENT.getEventName());
        postingEventRepository.save(event);

        // Ideally create new posting event each time
        postingEvent.setEventType(PostingEventTypeEnum.IN_PROCESS_POSTING_EVENT.getEventName());
        messagePublisher.sendMessage(postingEvent);
    }

    /**
     * This method accepts InProcessPostingEvent and creates PostingCommand with status CLEARED/FAILED
     * and publish completedEvent to MQ
     * @param postingEvent
     */
    @Transactional
    public void processPostingEvent(PostingEvent postingEvent) {

        String correlationId = postingEvent.getCorrelationId();

        // Retrieve wallets from the database
        Wallet fromWallet = walletRepository.findById(postingEvent.getFromWalletId()).get();
        Wallet toWallet = walletRepository.findById(postingEvent.getToWalletId()).get();

        // Validate again if the from wallet has sufficient balance.
        // TODO Optimistic locking to ensure action on latest data and not old version
        if (fromWallet.getBalance().compareTo(postingEvent.getTotalAmount()) < 0) {
            savePostingCommands(postingEvent, fromWallet, toWallet, correlationId, PostingCommand.PostingStatus.FAILED);
            savePostingQueries(postingEvent, fromWallet, toWallet, correlationId, PostingQuery.PostingStatus.FAILED);

            throw new InsufficientBalanceException("Insufficient balance in the wallet");
        }

        // create and save Cleared posting Commands meaning that transfer is successful
        savePostingCommands(postingEvent, fromWallet, toWallet, correlationId, PostingCommand.PostingStatus.CLEARED);
        savePostingQueries(postingEvent, fromWallet, toWallet, correlationId, PostingQuery.PostingStatus.CLEARED);

        // TODO - balance should be in posting table Update wallet balances
        fromWallet.setBalance(fromWallet.getBalance().subtract(postingEvent.getTotalAmount()));
        toWallet.setBalance(toWallet.getBalance().add(postingEvent.getTotalAmount()));
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        //convert to Event and store in eventStore
        Event event = conversionService.convert(postingEvent, Event.class);
        event.setEventType(PostingEventTypeEnum.COMPLETED_POSTING_EVENT.getEventName());
        postingEventRepository.save(event);

        event.setEventType(PostingEventTypeEnum.COMPLETED_POSTING_EVENT.getEventName());
        messagePublisher.sendMessage(event);

        //Broadcast the message as balance updated
        messagePublisher.sendMessage("Balance Updated for client " + fromWallet.getAccount().getClient());
    }

    @Override
    public PostingResponse validateAndSendMultiplePostingEvent(MultiplePostingEvent multiplePostingEvent) {

        walletValidator.validateMultipleTransferEvent(multiplePostingEvent);

        //TODO
        // post the create event to queue
        // create pending posting commands and post in process event to queue
        // process the transfer -> check if balance in fromWallet is >= summationOfAmount to transfer -> do transfer
        // else mark the posting failed
        return  new PostingResponse(LocalDateTime.now(), POSTING_EVENT_ACCEPTED_MSG);
    }

    private void savePostingCommands(PostingEvent postingEvent, Wallet fromWallet, Wallet toWallet, String correlationId, PostingCommand.PostingStatus status) {
        PostingCommand debitPostingCommand = createDebitTransaction(postingEvent, toWallet, correlationId);
        debitPostingCommand.setStatus(status);
        postingCommandRepository.save(debitPostingCommand);

        PostingCommand creditTransaction = createCreditTransaction(postingEvent, fromWallet, correlationId);
        creditTransaction.setStatus(status);
        postingCommandRepository.save(creditTransaction);
    }

    private PostingCommand createDebitTransaction(PostingEvent request, Wallet toWallet, String correlationId) {
        PostingCommand debitTransaction = new PostingCommand();
        debitTransaction.setCorrelationId(correlationId);
        debitTransaction.setWalletId(request.getFromWalletId());
        debitTransaction.setDescription("Transferred to " + toWallet.getWalletName());
        debitTransaction.setAmount(request.getTotalAmount().negate()); // Negative amount for debit
        debitTransaction.setTimestamp(LocalDateTime.now());

        return debitTransaction;
    }

    private PostingCommand createCreditTransaction(PostingEvent request, Wallet fromWallet, String correlationId) {
        PostingCommand creditTransaction = new PostingCommand();
        creditTransaction.setCorrelationId(correlationId);
        creditTransaction.setWalletId(request.getToWalletId());
        creditTransaction.setDescription("Transferred from " + fromWallet.getWalletName());
        creditTransaction.setAmount(request.getTotalAmount());
        creditTransaction.setTimestamp(LocalDateTime.now());

        return creditTransaction;
    }

    private void savePostingQueries(PostingEvent postingEvent, Wallet fromWallet, Wallet toWallet, String correlationId, PostingQuery.PostingStatus status) {
        PostingQuery debitPostingQuery = createDebitPostingQuery(postingEvent, toWallet, correlationId);
        debitPostingQuery.setStatus(status);
        postingQueryRepository.save(debitPostingQuery);

        PostingQuery creditPostingQuery = createCreditPostingQuery(postingEvent, fromWallet, correlationId);
        creditPostingQuery.setStatus(status);
        postingQueryRepository.save(creditPostingQuery);
    }

    private PostingQuery createDebitPostingQuery(PostingEvent request, Wallet toWallet, String correlationId) {
        PostingQuery debitTransaction = new PostingQuery();
        debitTransaction.setCorrelationId(correlationId);
        debitTransaction.setWalletId(request.getFromWalletId());
        debitTransaction.setDescription("Transferred to " + toWallet.getWalletName());
        debitTransaction.setAmount(request.getTotalAmount().negate()); // Negative amount for debit
        debitTransaction.setTimestamp(LocalDateTime.now());

        return debitTransaction;
    }

    private PostingQuery createCreditPostingQuery(PostingEvent request, Wallet fromWallet, String correlationId) {
        PostingQuery creditTransaction = new PostingQuery();
        creditTransaction.setCorrelationId(correlationId);
        creditTransaction.setWalletId(request.getToWalletId());
        creditTransaction.setDescription("Transferred from " + fromWallet.getWalletName());
        creditTransaction.setAmount(request.getTotalAmount());
        creditTransaction.setTimestamp(LocalDateTime.now());

        return creditTransaction;
    }

}
