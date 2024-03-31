package com.hsbc.ledger.handler;

import com.hsbc.ledger.dto.PostingEventTypeEnum;
import com.hsbc.ledger.dto.command.PostingEvent;
import com.hsbc.ledger.service.LedgerCommandService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostingEventHandler {

    @Autowired
    private final LedgerCommandService ledgerCommandService;
    private Logger logger = LoggerFactory.getLogger(PostingEventHandler.class);

    public void handleEvent(PostingEvent event) {
        logger.debug("Handling postingEvent with status={0} " , event.getEventType());
        if(event.getEventType().equals(PostingEventTypeEnum.CREATE_POSTING_EVENT.getEventName())){
            ledgerCommandService.createPostingCommand(event);
        } else if(event.getEventType().equals(PostingEventTypeEnum.IN_PROCESS_POSTING_EVENT.getEventName())){
            ledgerCommandService.processPostingEvent(event);
        } else{
           logger.info("PostingEvent with status={0} and hence no action required", event.getEventType());
        }

    }
}
