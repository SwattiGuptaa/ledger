package com.hsbc.ledger.dto.query;

import java.math.BigDecimal;

public record WalletLedgerDTO(
        Long walletId,
        String walletName,
        BigDecimal balance) {
}
