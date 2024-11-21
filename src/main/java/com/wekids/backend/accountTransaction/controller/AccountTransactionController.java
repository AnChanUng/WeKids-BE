package com.wekids.backend.accountTransaction.controller;

import com.wekids.backend.accountTransaction.dto.request.TransactionRequest;
import com.wekids.backend.accountTransaction.dto.request.UpdateMemoRequest;
import com.wekids.backend.accountTransaction.dto.response.TransactionDetailSearchResponse;
import com.wekids.backend.accountTransaction.service.AccountTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class AccountTransactionController {

    private final AccountTransactionService accountTransactionService;

    @PostMapping("/{transactionId}/memo")
    public ResponseEntity<Void> saveMemo(@PathVariable("transactionId") Long transactionId, @RequestBody @Valid UpdateMemoRequest request) {
        accountTransactionService.saveMemo(transactionId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailSearchResponse> showTransactionDetails(@PathVariable("transactionId") Long transactionId) {
        TransactionDetailSearchResponse result = accountTransactionService.findByTransactionId(transactionId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> postTransaction(@RequestBody TransactionRequest transactionRequest) {
        accountTransactionService.saveTransaction(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
