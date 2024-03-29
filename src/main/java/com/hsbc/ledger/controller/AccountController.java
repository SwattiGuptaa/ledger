package com.hsbc.ledger.controller;

import com.hsbc.ledger.exception.AccountNotFoundException;
import com.hsbc.ledger.exception.InvalidAccountStatusException;
import com.hsbc.ledger.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("account")
public class AccountController {

    @Autowired
    private AccountService accountService;


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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @ExceptionHandler({AccountNotFoundException.class, InvalidAccountStatusException.class})
    public ResponseEntity<String> handleCustomException(RuntimeException e) {
        // Handle Exception and return appropriate error response
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
