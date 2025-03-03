package com.rehan.tinyledger.core.service;


import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionRequest;

import java.math.BigDecimal;
import java.util.List;

public interface LedgerService {
    Transaction recordTransaction(TransactionRequest request);

    BigDecimal getBalance();

    List<Transaction> getTransactionHistory();
}
