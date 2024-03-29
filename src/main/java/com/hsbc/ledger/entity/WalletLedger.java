package com.hsbc.ledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * This entity will get data from view which is scheduled to run once a day to reconcile.
 * So for every wallet there will be at most 365 entries in a year
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class WalletLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    Long walletId;
    String walletName;
    BigDecimal balance;
    Date reconcileDate;
}
