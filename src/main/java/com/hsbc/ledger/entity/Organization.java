package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_name")
    private String organizationName;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Account> accounts;

    public Organization(String organizationName) {
        this.organizationName = organizationName;
    }
}
