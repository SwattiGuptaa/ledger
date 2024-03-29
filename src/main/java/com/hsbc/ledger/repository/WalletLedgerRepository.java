package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.WalletLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WalletLedgerRepository extends JpaRepository<WalletLedger, Long> {

    List<WalletLedger> findByWalletIdAndReconcileDateLessThanEqual(Long walletId, LocalDate reconcileDate);
}
