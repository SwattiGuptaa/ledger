package com.hsbc.ledger.controller.query;

import com.hsbc.ledger.dto.query.WalletLedgerDTO;
import com.hsbc.ledger.exception.WalletNotFoundException;
import com.hsbc.ledger.service.LedgerQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/ledger")
@ConditionalOnProperty(name = "ledger.write.enabled", havingValue = "false")
public class LedgerQueryController {

    @Autowired
    private LedgerQueryService ledgerQueryService;


    @Operation(summary = "Fetches the ledger for provided walletId",
            description = "Return Ledger for given WalletId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = WalletLedgerDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error encountered at server side")
    })
    @GetMapping("/v1/walletLedger/{walletId}")
    public ResponseEntity<?> getCurrentBalanceForWallet(@PathVariable Long walletId) {

        try {
            WalletLedgerDTO ledger = ledgerQueryService.queryWalletLedger(walletId);
            return ResponseEntity.ok().body(ledger);
        } catch (WalletNotFoundException e) {
            return ResponseEntity.badRequest().body("WalletNotFound");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Fetches the ledger for provided walletId until the timestamp provided",
            description = "Return Ledger for given WalletId until the timestamp provided. Timestamp should be provided in format ISO.DATE_TIME"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request successful", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = WalletLedgerDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Error encountered at server side")
    })
    @GetMapping("/v1/walletLedgerAtTimestamp/{walletId}")
    public ResponseEntity<?> getHistoricalBalanceForWallet(@PathVariable Long walletId,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp) {
        try {
            WalletLedgerDTO ledger = ledgerQueryService.queryWalletLedgerUntilTime(walletId, timestamp);
            return ResponseEntity.ok().body(ledger);
        } catch (WalletNotFoundException e) {
            return ResponseEntity.badRequest().body("WalletNotFound");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
