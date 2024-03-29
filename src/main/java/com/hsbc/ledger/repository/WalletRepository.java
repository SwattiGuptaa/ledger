package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByIdIn(Set<Long> toWalletIds);


}
