package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostingEventRepository extends JpaRepository<Event, Long> {



}


