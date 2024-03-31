package com.hsbc.ledger.controller;

import com.hsbc.ledger.exception.AccountNotFoundException;
import com.hsbc.ledger.exception.InvalidAccountStatusException;
import com.hsbc.ledger.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Operation(summary = "Updates the Account status",
            description = "An OPEN account can be requested to be CLOSED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error encountered at server side")
    })
    @PutMapping("/v1/updateStatus/{accountId}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable Long accountId, @RequestParam String status) {
        try {
            accountService.updateAccountStatus(accountId, status);
            return ResponseEntity.ok().body("Account updated");
        } catch (AccountNotFoundException | InvalidAccountStatusException e) {
            return handleCustomException(e);
        }  catch (Exception e) {
            logger.error("Exception occurred ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @ExceptionHandler({AccountNotFoundException.class, InvalidAccountStatusException.class})
    public ResponseEntity<String> handleCustomException(RuntimeException e) {
        logger.error("Bad request:Exception occurred", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
