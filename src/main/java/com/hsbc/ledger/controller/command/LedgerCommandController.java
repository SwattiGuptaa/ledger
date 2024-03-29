package com.hsbc.ledger.controller.command;

import com.hsbc.ledger.dto.command.MultiplePostingEvent;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.dto.command.SinglePostingResponse;
import com.hsbc.ledger.exception.AccountClosedException;
import com.hsbc.ledger.exception.InsufficientBalanceException;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.service.LedgerCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@NoArgsConstructor
@RequestMapping("/ledger")
@ConditionalOnProperty(name = "ledger.write.enabled", havingValue = "true")
public class LedgerCommandController {

    @Autowired
    private LedgerCommandService ledgerCommandService;

    Logger logger = LoggerFactory.getLogger(LedgerCommandController.class);

    @Operation(summary = "Transfer money from one wallet to other ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Transfer request accepted", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SinglePostingResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error encountered at server side")
    })
    @PostMapping("/v1/wallet/transfer")
    public ResponseEntity<?> createPosting(@Valid @RequestBody PostingEvent postingEvent) {
        try {
            SinglePostingResponse response = ledgerCommandService.validateAndSendPostingEvent(postingEvent);
            return ResponseEntity.accepted().body(response);
        } catch (WalletNotFoundException | InsufficientBalanceException | AccountClosedException e) {
            return handleCustomException(e);
        } catch (Exception e) {
            logger.error("Exception occurred ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(summary = "Transfer money from one wallet to multiple wallets ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Transfer request accepted", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = SinglePostingResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error encountered at server side")
    })
    @PostMapping("/v1/wallets/transfer")
    public ResponseEntity<?> createPostings(@Valid @RequestBody MultiplePostingEvent multiplePostingEvent) {
        try {
            //SinglePostingResponse response = ledgerCommandService.validateAndSendPostingEvent(postingEvent);
            return ResponseEntity.accepted().body("");
        } catch (WalletNotFoundException | InsufficientBalanceException | AccountClosedException e) {
            return handleCustomException(e);
        } catch (Exception e) {
            logger.error("Exception occurred ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @ExceptionHandler({WalletNotFoundException.class, InsufficientBalanceException.class, AccountClosedException.class})
    public ResponseEntity<String> handleCustomException(RuntimeException e) {
        // Handle Exception and return appropriate error response
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
