package com.rehan.tinyledger.adapter.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rehan.tinyledger.core.domain.BalanceResponse;
import com.rehan.tinyledger.core.domain.Transaction;
import com.rehan.tinyledger.core.domain.TransactionRequest;
import com.rehan.tinyledger.core.domain.TransactionType;
import com.rehan.tinyledger.core.service.LedgerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LedgerController.class)
class LedgerControllerTest {
    private static
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerService ledgerService;
    @BeforeAll
    public static void setup()
    {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    }
    @Test
    void shouldRecordTransaction() throws Exception {
       
        TransactionRequest request = new TransactionRequest(
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit"
        );
        
        UUID fixedId = UUID.randomUUID();
        Transaction transaction = new Transaction(
                fixedId,
                TransactionType.DEPOSIT,
                new BigDecimal("100.00"),
                "Test deposit",
                LocalDateTime.now()
        );
        
        when(ledgerService.recordTransaction(any(TransactionRequest.class))).thenReturn(transaction);

       
        String response=mockMvc.perform(post("/api/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Transaction transactionResponse = objectMapper.readValue(response, Transaction.class);
       
        assertEquals(transactionResponse.amount(),new BigDecimal("100.00"));
        assertEquals(transactionResponse.type(),TransactionType.DEPOSIT);
        assertEquals(transactionResponse.description(),"Test deposit");
    }

    @Test
    void shouldGetBalance() throws Exception {
       
        when(ledgerService.getBalance()).thenReturn(new BigDecimal("120.00"));

       
        String response=mockMvc.perform(get("/api/ledger/balance"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BalanceResponse balanceResponse = objectMapper.readValue(response, BalanceResponse.class);
        assertEquals(balanceResponse.balance(),new BigDecimal("120.00"));

    }

    @Test
    void shouldGetTransactionHistory() throws Exception {
       
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), TransactionType.DEPOSIT, new BigDecimal("100.00"), "Deposit 1", now)
        );
        
        when(ledgerService.getTransactionHistory()).thenReturn(transactions);

       
        String response = mockMvc.perform(get("/api/ledger/transactions"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Transaction> transactionsList = objectMapper.readValue(response, new TypeReference<>() {
        });
       
        assertEquals(transactions.size(),1);
        Transaction transaction=transactionsList.get(0);
        assertEquals(transaction.amount(),new BigDecimal("100.00"));
        assertEquals(transaction.type(),TransactionType.DEPOSIT);
        assertEquals(transaction.description(),"Deposit 1");

    }

    @Test
    public void testNotFoundExceptionWithMessage() throws Exception {
        mockMvc.perform(get("/api/ledger/transaction"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertTrue(responseBody.contains(ErrorMessages.NOT_FOUND.getMessage()));
                });
    }
}
