package com.hsbc.ledger.converter;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.entity.Event;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostingEventToPostingEventStoreConverterTest {

    private final PostingEventToPostingEventStoreConverter converter = new PostingEventToPostingEventStoreConverter();

    @Test
    public void testConvert() {
        // Sample PostingEvent
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);
        postingEvent.setToWalletId(2L);
        postingEvent.setTotalAmount(BigDecimal.valueOf(10));
        postingEvent.setCorrelationId(UUID.randomUUID().toString());

        // Convert the PostingEvent to Event
        Event event = converter.convert(postingEvent);

        // Verify that the conversion was successful
        assertNotNull(event);
        assertEquals(postingEvent.getFromWalletId(), event.getFromWalletId());
        assertEquals(postingEvent.getToWalletId(), event.getToWalletId());
        assertEquals(postingEvent.getTotalAmount(), event.getTotalAmount());
        assertEquals(postingEvent.getCorrelationId(), event.getCorrelationId());
    }

}