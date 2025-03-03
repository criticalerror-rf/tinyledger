package com.rehan.tinyledger.adapter.persistance;


import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLedgerRepositoryTest {

    private InMemoryLedgerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLedgerRepository();
    }

    @Test
    void shouldSaveTransaction() {
       
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(
                id,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit",
                LocalDateTime.now()
        );

       
        Transaction saved = repository.save(transaction);

       
        assertEquals(transaction, saved);
        assertTrue(repository.findById(id).isPresent());
        assertEquals(transaction, repository.findById(id).get());
    }

    @Test
    void shouldFindAllTransactions() {
       
        Transaction transaction1 = new Transaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit 1",
                LocalDateTime.now()
        );
        
        Transaction transaction2 = new Transaction(
                UUID.randomUUID(),
                TransactionType.WITHDRAWAL,
                new BigDecimal("50.00"),
                "Test withdrawal",
                LocalDateTime.now()
        );
        
        repository.save(transaction1);
        repository.save(transaction2);

       
        List<Transaction> transactions = repository.findAll();

       
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
    }

    @Test
    void shouldFindTransactionById() {
       
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(
                id,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit",
                LocalDateTime.now()
        );
        
        repository.save(transaction);

       
        Optional<Transaction> found = repository.findById(id);

       
        assertTrue(found.isPresent());
        assertEquals(transaction, found.get());
    }

    @Test
    void shouldReturnNullWhenTransactionNotFound() {
       
        UUID nonExistentId = UUID.randomUUID();

       
        Optional<Transaction> found = repository.findById(nonExistentId);

       
        assertNull(found.orElse(null));
    }
}
