package com.rehan.tinyledger.adapter.persistance;

import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.port.LedgerRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLedgerRepository implements LedgerRepository {
    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();


    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.id(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        return transactions.values().stream()
                .sorted(Comparator.comparing(Transaction::timestamp).reversed())
                .toList();
    }

}