package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.PostingQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostingQueryRepository extends JpaRepository<PostingQuery, Long> {

    List<PostingQuery> findByWalletIdAndStatusAndTimestampBetween (Long walletId, PostingQuery.PostingStatus status, LocalDateTime startOfDay, LocalDateTime now);

}