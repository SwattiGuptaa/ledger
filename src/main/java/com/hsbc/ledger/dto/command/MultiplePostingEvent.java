package com.hsbc.ledger.dto.command;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiplePostingEvent {

    private String correlationId;
    private String eventType;
    @NonNull
    private Long fromWalletId;
    private Map<String, BigDecimal> destinationWallets;

}
