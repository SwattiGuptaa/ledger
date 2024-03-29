package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal balance;

    private String walletName;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type")
    private WalletType walletType;

    public enum WalletType {
        FIAT_CURRENCY,
        CRYPTO,
        STOCK,
        BOND
    }

    public Wallet(Long id, String walletName) {
        this.id = id;
        this.walletName = walletName;
    }
}
