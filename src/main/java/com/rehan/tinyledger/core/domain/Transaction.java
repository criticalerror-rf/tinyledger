package com.rehan.tinyledger.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

public record Transaction(
        @JsonProperty("id") UUID id,
        @JsonProperty("type") TransactionType type,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("description") String description,
        @JsonProperty("timestamp") LocalDateTime timestamp
) {
    public static Transaction create(TransactionType type, BigDecimal amount, String description) {
        return new Transaction(
                UUID.randomUUID(),
                type,
                amount.setScale(2, RoundingMode.UNNECESSARY),
                description,
                LocalDateTime.now()
        );
    }
}
