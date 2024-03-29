package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.query.WalletLedgerDTO;

import java.time.LocalDateTime;

public interface LedgerQueryService {
    WalletLedgerDTO queryWalletLedger(Long walletId);
    WalletLedgerDTO queryWalletLedgerUntilTime(Long walletId, LocalDateTime timestamp);

}
