package com.hsbc.ledger.controller.command;


import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.dto.command.PostingResponse;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.service.LedgerCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class LedgerCommandControllerTest {

    @Mock
    private LedgerCommandService ledgerCommandService;

    @InjectMocks
    private LedgerCommandController ledgerCommandController;

    private static final String POSTING_EVENT_ACCEPTED_MSG = "Posting event Accepted";


    @Test
    public void testCreatePosting_Successful() {
        // Mock behavior of ledgerCommandService
        PostingEvent postingEvent = createPostingEvent();

        PostingResponse response = new PostingResponse(LocalDateTime.now(), POSTING_EVENT_ACCEPTED_MSG);

        when(ledgerCommandService.validateAndSendPostingEvent(postingEvent)).thenReturn(response);

        ResponseEntity<PostingResponse> responseEntity = (ResponseEntity<PostingResponse>) ledgerCommandController.createPosting(postingEvent);

        Mockito.verify(ledgerCommandService).validateAndSendPostingEvent(postingEvent);
        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(POSTING_EVENT_ACCEPTED_MSG, responseEntity.getBody().getMessage());
    }



    @Test
    public void testCreatePosting_ExceptionHandling() {
        // Mock behavior of ledgerCommandService to throw an exception
        PostingEvent postingEvent = createPostingEvent();
        when(ledgerCommandService.validateAndSendPostingEvent(postingEvent)).thenThrow(new WalletNotFoundException("From wallet not found"));

        // Call the method under test
        ResponseEntity<?> responseEntity = ledgerCommandController.createPosting(postingEvent);
        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    private static PostingEvent createPostingEvent() {
        PostingEvent postingEvent = new PostingEvent();
        postingEvent.setFromWalletId(1L);
        postingEvent.setToWalletId(2L);
        postingEvent.setTotalAmount(BigDecimal.valueOf(20));
        return postingEvent;
    }


}
