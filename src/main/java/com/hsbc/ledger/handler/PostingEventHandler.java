package com.hsbc.ledger.handler;

import com.hsbc.ledger.dto.PostingEventTypeEnum;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.service.LedgerCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostingEventHandler {

    @Autowired
    private final LedgerCommandService ledgerCommandService;

    public void handleEvent(PostingEvent event) {
        if(event.getEventType().equals(PostingEventTypeEnum.CREATE_POSTING_EVENT.getEventName())){
            ledgerCommandService.createPostingCommand(event);
        } else if(event.getEventType().equals(PostingEventTypeEnum.IN_PROCESS_POSTING_EVENT.getEventName())){
            ledgerCommandService.processPostingEvent(event);
        } else{
            // just log the process is completed
        }

    }
}
