package com.rehan.tinyledger.core.service;

import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionRequest;
import com.rehan.tinyledger.core.domain.TransactionType;
import com.rehan.tinyledger.core.domain.exception.InsufficientFundsException;
import com.rehan.tinyledger.core.domain.exception.TransactionFailedException;
import com.rehan.tinyledger.core.port.LedgerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LedgerServiceImpl implements LedgerService {
    private final LedgerRepository ledgerRepository;

    public LedgerServiceImpl(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Override
    public Transaction recordTransaction(TransactionRequest request) {
        if(request.amount().compareTo(new BigDecimal(0))<=0)
            throw new TransactionFailedException("The transaction failed. Invalid amount");

        switch (request.type()) {
            case DEPOSIT -> {
                Transaction transaction = Transaction.create(
                        request.type(),
                        request.amount().setScale(2, RoundingMode.UNNECESSARY),
                        request.description()
                );

                return ledgerRepository.save(transaction);

            }
            case WITHDRAWAL -> {
                if (computeBalance().compareTo(request.amount()) < 0) {
                    throw new InsufficientFundsException("Insufficient funds for withdrawal");
                }
                Transaction transaction = Transaction.create(
                        request.type(),
                        request.amount().setScale(2, RoundingMode.UNNECESSARY),
                        request.description()
                );
                return ledgerRepository.save(transaction);


            }
            default -> throw new TransactionFailedException("The transaction failed");
        }
    }

    @Override
    public BigDecimal getBalance() {
        return computeBalance();
    }

    @Override
    public List<Transaction> getTransactionHistory() {
        return ledgerRepository.findAll().stream()
                .sorted(Comparator.comparing(Transaction::timestamp).reversed())
                .toList();
    }

    private BigDecimal computeBalance() {
        return ledgerRepository.findAll().stream()
                .map(transaction -> switch (transaction.type()) {
                    case DEPOSIT -> transaction.amount();
                    case WITHDRAWAL -> transaction.amount().negate();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.UNNECESSARY);
    }
}
