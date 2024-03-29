/*
 * Read replica of Posting command table - for simplicity just inserting data in both at same time
 */
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
public class PostingQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correlation_Id")
    private String correlationId;

    @Column(name = "wallet_id")
    private Long walletId;

    private String description;

    private BigDecimal amount;

    @Column(name = "created_Timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "posting_status")
    private PostingStatus status;

    public enum PostingStatus {
        PENDING,
        CLEARED,
        FAILED
    }
}
