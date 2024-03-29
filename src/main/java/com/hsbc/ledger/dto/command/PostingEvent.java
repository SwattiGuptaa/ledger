package com.hsbc.ledger.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;

@Component
@Getter
@Setter
public class PostingEvent implements Serializable {

    private String correlationId;
    @NotBlank(message = "FromWalletId must not be blank")
    private Long fromWalletId;
    @NotBlank(message = "toWalletId must not be blank")
    private Long toWalletId;
    @Positive
    private BigDecimal totalAmount;
    private String eventType;

    @Override
    public String toString() {
        return "PostingEvent{" +
                "correlationId='" + correlationId + '\'' +
                ", fromWalletId=" + fromWalletId +
                ", toWalletId=" + toWalletId +
                ", totalAmount=" + totalAmount +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
