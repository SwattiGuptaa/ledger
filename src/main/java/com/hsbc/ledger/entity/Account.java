package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "account_name")
    private String accountName;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Wallet> wallets;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus status;

    // Constructors, getters, and setters

    public enum AccountStatus {
        PENDING,
        OPEN,
        CLOSED,
        FROZEN
    }
}
