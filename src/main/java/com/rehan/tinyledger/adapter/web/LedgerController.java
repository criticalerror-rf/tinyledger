package com.rehan.tinyledger.adapter.web;


import com.rehan.tinyledger.core.domain.BalanceResponse;
import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionRequest;
import com.rehan.tinyledger.core.service.LedgerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {
    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> recordTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = ledgerService.recordTransaction(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        return ResponseEntity.ok(new BalanceResponse(ledgerService.getBalance()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory() {
        return ResponseEntity.ok(ledgerService.getTransactionHistory());
    }
}
