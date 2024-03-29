package com.hsbc.ledger.rmq.subscribe;

import com.hsbc.ledger.handler.PostingEventHandler;
import com.hsbc.ledger.dto.command.PostingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hsbc.ledger.util.Constants.POSTING_QUERY_QUEUE;
import static com.hsbc.ledger.util.Constants.WALLET_POSTING_QUEUE;

@Component
@RequiredArgsConstructor
public class PostingListener {

    @Autowired
    private final PostingEventHandler postingEventHandler;



    @RabbitListener(queues = WALLET_POSTING_QUEUE)
    public void processPostingEvent(PostingEvent postingEvent) {
        postingEventHandler.handleEvent(postingEvent);
    }

    @RabbitListener(queues = POSTING_QUERY_QUEUE)
    public void readMessage(PostingEvent message) {
        System.out.println("Message received: " + message);
    }
}
