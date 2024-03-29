package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.PostingCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostingCommandRepository extends JpaRepository<PostingCommand, Long> {


}


