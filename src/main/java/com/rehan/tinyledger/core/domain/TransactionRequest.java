package com.rehan.tinyledger.core.domain;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount,
        String description
) {
}
