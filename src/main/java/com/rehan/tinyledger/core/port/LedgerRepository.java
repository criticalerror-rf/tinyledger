package com.rehan.tinyledger.core.port;

import com.rehan.tinyledger.core.domain.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepository {
    Transaction save(Transaction transaction);

    List<Transaction> findAll();

    Optional<Transaction> findById(UUID id);
}
