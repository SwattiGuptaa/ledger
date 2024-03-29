package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correlationId;

    @Column(name = "wallet_id")
    private Long walletId;

    private String description;

    private BigDecimal amount;

    @Column(name = "created_Timestamp")
    private LocalDateTime timestamp;

}

