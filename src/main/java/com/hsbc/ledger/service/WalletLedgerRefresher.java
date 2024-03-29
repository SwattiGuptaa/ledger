package com.hsbc.ledger.service;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WalletLedgerRefresher {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // Runs daily at 1:00 AM
    public void refresh(){
        this.entityManager.createNativeQuery("call daily_wallet_ledger_reconciliation();").executeUpdate();
    }

}
