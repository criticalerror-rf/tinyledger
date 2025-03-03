package com.rehan.tinyledger.core.service;


import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionRequest;
import com.rehan.tinyledger.core.domain.TransactionType;
import com.rehan.tinyledger.core.domain.exception.InsufficientFundsException;
import com.rehan.tinyledger.core.domain.exception.TransactionFailedException;
import com.rehan.tinyledger.core.port.LedgerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LedgerServiceImplTest {

    @Mock
    private LedgerRepository ledgerRepository;

    private LedgerService ledgerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ledgerService = new LedgerServiceImpl(ledgerRepository);
    }

    @Test
    void shouldRecordDepositTransaction() {
       
        TransactionRequest request = new TransactionRequest(
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit"
        );

        UUID fixedId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Transaction savedTransaction = new Transaction(
                fixedId,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit",
                now
        );

        when(ledgerRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

       
        Transaction result = ledgerService.recordTransaction(request);


        assertEquals(fixedId, result.id());
        assertEquals(TransactionType.DEPOSIT, result.type());
        assertEquals(new BigDecimal("100.00"), result.amount());
        assertEquals("Test deposit", result.description());
    }

    @Test
    void shouldRecordWithdrawalTransaction() {
       
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("100.00"), "Deposit 1", now),
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("50.00"), "Deposit 2", now),
                new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, new BigDecimal("30.00"), "Withdrawal 1", now)
        );
        when(ledgerRepository.findAll()).thenReturn(transactions);

        TransactionRequest withdrawalRequest = new TransactionRequest(
                TransactionType.WITHDRAWAL,
                new BigDecimal("100.00"),
                "Test Withdrawal"
        );

        UUID fixedId = UUID.randomUUID();

        Transaction savedTransaction = new Transaction(
                fixedId,
                TransactionType.WITHDRAWAL,
                new BigDecimal("100.00"),
                "Test Withdrawal",
                now
        );

        when(ledgerRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

       
        Transaction result = ledgerService.recordTransaction(withdrawalRequest);

       
        assertEquals(fixedId, result.id());
        assertEquals(TransactionType.WITHDRAWAL, result.type());
        assertEquals(new BigDecimal("100.00"), result.amount());
        assertEquals("Test Withdrawal", result.description());
    }

    @Test
    void shouldCalculateBalance() {
       
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("100.00"), "Deposit 1", now),
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("50.00"), "Deposit 2", now),
                new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, new BigDecimal("30.00"), "Withdrawal 1", now)
        );

        when(ledgerRepository.findAll()).thenReturn(transactions);

       
        BigDecimal balance = ledgerService.getBalance();

       
        assertEquals(new BigDecimal("120.00"), balance);
    }

    @Test
    void shouldReturnTransactionHistory() {
       
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("100.00"), "Deposit 1", now.minusDays(2)),
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("50.00"), "Deposit 2", now.minusDays(1)),
                new Transaction(UUID.randomUUID(), TransactionType.WITHDRAWAL, new BigDecimal("30.00"), "Withdrawal 1", now)
        );

        when(ledgerRepository.findAll()).thenReturn(transactions);

       
        List<Transaction> history = ledgerService.getTransactionHistory();

       
        assertEquals(3, history.size());
       
        assertEquals("Withdrawal 1", history.get(0).description());
    }



    @Test
    void insufficientBalance() {
       
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("100.00"), "Deposit 1", now.minusDays(2)),
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("50.00"), "Deposit 2", now.minusDays(1))
        );
        when(ledgerRepository.findAll()).thenReturn(transactions);


        TransactionRequest withdrawalRequest = new TransactionRequest(TransactionType.WITHDRAWAL, new BigDecimal("200.0"), "Withdrawal 1");
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            ledgerService.recordTransaction(withdrawalRequest);
        });

        assertEquals("Insufficient funds for withdrawal", exception.getMessage());
    }

    @Test
    void negativeAmount() {


        TransactionRequest withdrawalRequest = new TransactionRequest(TransactionType.WITHDRAWAL, new BigDecimal("-200.0"), "Withdrawal 1");
        TransactionFailedException exception = assertThrows(TransactionFailedException.class, () -> {
            ledgerService.recordTransaction(withdrawalRequest);
        });

        assertEquals("The transaction failed. Invalid amount", exception.getMessage());
    }
}
