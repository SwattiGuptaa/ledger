package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByIdIn(List<Long> toWalletIds);


}
