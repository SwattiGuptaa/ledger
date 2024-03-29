package com.hsbc.ledger.rmq.publish;

import com.hsbc.ledger.dto.command.PostingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hsbc.ledger.util.Constants.WALLET_POSTING_ROUTING_KEY;

@Component
@RequiredArgsConstructor
public class MessagePublisher {
    @Autowired
    private final RabbitTemplate rabbitTemplate;

    public <T> void sendMessage(T message) {

        if (message instanceof PostingEvent event) {
            rabbitTemplate.convertAndSend(WALLET_POSTING_ROUTING_KEY, event);
        } else if (message instanceof String msg) {
            rabbitTemplate.convertAndSend("client1.fanout.exchange", "", msg);
        }
    }
}
