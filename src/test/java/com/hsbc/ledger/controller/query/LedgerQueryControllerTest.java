package com.hsbc.ledger.controller.query;

import com.hsbc.ledger.dto.query.WalletLedgerDTO;
import com.hsbc.ledger.service.LedgerQueryService;
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
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class LedgerQueryControllerTest {

    @Mock
    private LedgerQueryService ledgerQueryService;

    @InjectMocks
    private LedgerQueryController ledgerQueryController;



    @Test
    public void testGetCurrentBalanceForWallet_Successful() {

        Long walletId = 1L;
        BigDecimal balance = BigDecimal.valueOf(100);

        WalletLedgerDTO dto = new WalletLedgerDTO(1L, "Curreny wallet",balance);

        when(ledgerQueryService.queryWalletLedger(walletId)).thenReturn(dto);

        ResponseEntity<WalletLedgerDTO> responseEntity = (ResponseEntity<WalletLedgerDTO>) ledgerQueryController.getCurrentBalanceForWallet(walletId);

        Mockito.verify(ledgerQueryService).queryWalletLedger(walletId);
        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(balance, responseEntity.getBody().balance());
    }


    @Test
    public void testGetHistoricalBalanceForWallet_Successful() {

        Long walletId = 1L;

        String timestampString = "2024-03-03 12:30:45";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime timestamp = LocalDateTime.parse(timestampString, formatter);

        BigDecimal balance = BigDecimal.valueOf(100);

        WalletLedgerDTO dto = new WalletLedgerDTO(1L, "Curreny wallet",balance);

        when(ledgerQueryService.queryWalletLedger(walletId)).thenReturn(dto);

        ResponseEntity<WalletLedgerDTO> responseEntity = (ResponseEntity<WalletLedgerDTO>) ledgerQueryController.getHistoricalBalanceForWallet(walletId, timestamp);

        Mockito.verify(ledgerQueryService).queryWalletLedgerUntilTime(walletId, timestamp);
        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(balance, responseEntity.getBody().balance());
    }

}
