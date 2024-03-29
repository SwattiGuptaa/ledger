package com.hsbc.ledger.dto.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class SinglePostingResponse {

    private LocalDateTime timestamp;
    private String message;


}
