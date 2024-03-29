package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.dto.command.SinglePostingResponse;

public interface LedgerCommandService {
    SinglePostingResponse validateAndSendPostingEvent(PostingEvent postingEvent);
    void processPostingEvent(PostingEvent event);
    void createPostingCommand(PostingEvent postingEvent);
}
