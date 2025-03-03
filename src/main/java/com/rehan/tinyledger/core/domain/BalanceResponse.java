package com.rehan.tinyledger.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record BalanceResponse(@JsonProperty("balance") BigDecimal balance) {
}
