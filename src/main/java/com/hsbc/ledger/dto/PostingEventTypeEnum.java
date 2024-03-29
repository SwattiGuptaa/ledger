package com.hsbc.ledger.dto;

import lombok.Getter;

@Getter
public enum PostingEventTypeEnum {
    CREATE_POSTING_EVENT("CreatePostingEvent"),
    IN_PROCESS_POSTING_EVENT("InProcessPostingEVent"),
    COMPLETED_POSTING_EVENT("CompletedPostingEvent");

    private final String eventName;

    PostingEventTypeEnum(String eventName) {
        this.eventName = eventName;
    }
}
