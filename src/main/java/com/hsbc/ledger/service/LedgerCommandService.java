package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.command.MultiplePostingEvent;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.dto.command.PostingResponse;

public interface LedgerCommandService {
    PostingResponse validateAndSendPostingEvent(PostingEvent postingEvent);
    void createPostingCommand(PostingEvent postingEvent);
    void processPostingEvent(PostingEvent event);

    PostingResponse validateAndSendMultiplePostingEvent(MultiplePostingEvent multiplePostingEvent);


}
