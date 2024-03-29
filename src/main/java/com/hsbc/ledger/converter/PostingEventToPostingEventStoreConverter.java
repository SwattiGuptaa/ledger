package com.hsbc.ledger.converter;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.entity.Event;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class PostingEventToPostingEventStoreConverter implements Converter<PostingEvent, Event> {

    /**
     * Method to convert PostingEvent to Event so that it can be stored in event store
     * @param postingEvent - posting event
     * @return - Event
     */
    @Override
    public Event convert(PostingEvent postingEvent){
        Event event = new Event();

        event.setFromWalletId(postingEvent.getFromWalletId());
        event.setToWalletId(postingEvent.getToWalletId());
        event.setTotalAmount(postingEvent.getTotalAmount());

        String correlationId = postingEvent.getCorrelationId();

        correlationId = Objects.nonNull(correlationId) ? correlationId : UUID.randomUUID().toString();
        event.setCorrelationId(correlationId);

        return event;
    }

}
