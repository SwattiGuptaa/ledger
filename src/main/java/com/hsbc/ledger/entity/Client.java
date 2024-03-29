package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name")
    private String clientName;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private Account account;

    public Client(String clientName) {
        this.clientName = clientName;
    }
}
