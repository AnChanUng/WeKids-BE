package com.wekids.backend.accountTransaction.service;

import com.wekids.backend.account.domain.Account;
import com.wekids.backend.account.domain.enums.AccountState;
import com.wekids.backend.account.repository.AccountRepository;
import com.wekids.backend.accountTransaction.domain.AccountTransaction;
import com.wekids.backend.accountTransaction.domain.enums.TransactionType;
import com.wekids.backend.accountTransaction.dto.request.TransactionRequest;
import com.wekids.backend.accountTransaction.dto.request.UpdateMemoRequest;
import com.wekids.backend.accountTransaction.dto.response.TransactionDetailSearchResponse;
import com.wekids.backend.accountTransaction.repository.AccountTransactionRepository;
import com.wekids.backend.exception.ErrorCode;
import com.wekids.backend.exception.WekidsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AccountTransactionServiceImpl implements AccountTransactionService {

    private final AccountTransactionRepository accountTransactionRepository;
    private final AccountRepository accountRepository;


    @Override
    public TransactionDetailSearchResponse findByTransactionId(Long transactionId) {

        AccountTransaction accountTransaction = findAccountTransactionById(transactionId, "transaction id값");
        return TransactionDetailSearchResponse.from(accountTransaction);

    }

    @Override
    @Transactional
    public void saveMemo(Long transactionId, UpdateMemoRequest request) {
        AccountTransaction accountTransaction = findAccountTransactionById(transactionId, "memo업데이트를 하는 거래내역id");
        accountTransaction.updateMemo(request.getMemo());
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionRequest transactionRequest) {
        Account parentAccount = findAccountByAccountNumber(transactionRequest.getParentAccountNumber());
        Account childAccount = findAccountByAccountNumber(transactionRequest.getChildAccountNumber());

        log.info(String.valueOf(parentAccount));
        log.info(String.valueOf(childAccount));

        validateTransaction(transactionRequest, parentAccount, childAccount);

        AccountTransaction parentTransaction = createParentAccountTransaction(transactionRequest, parentAccount);
        AccountTransaction childTransaction = createChildAccountTransaction(transactionRequest, childAccount);

        accountTransactionRepository.save(parentTransaction);
        accountTransactionRepository.save(childTransaction);
    }


    private Account findAccountByAccountNumber(String accountNumber) {
        log.debug("Searching for account with account number: {}", accountNumber);  // 로그 추가
        return accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new WekidsException(ErrorCode.MEMBER_NOT_FOUND,
                        "회원 계좌번호: " + accountNumber));
    }

    private AccountTransaction createParentAccountTransaction(TransactionRequest request, Account parentAccount) {
        BigDecimal newBalance = parentAccount.getBalance().subtract(request.getAmount());
        parentAccount.updateAccountAmount(newBalance);
        return createTransaction(request, TransactionType.WITHDRAWAL, newBalance, parentAccount);
    }

    private AccountTransaction createChildAccountTransaction(TransactionRequest request, Account childAccount) {
        BigDecimal newBalance = childAccount.getBalance().add(request.getAmount());
        childAccount.updateAccountAmount(newBalance);
        return createTransaction(request, TransactionType.DEPOSIT, newBalance, childAccount);
    }


    private AccountTransaction createTransaction(TransactionRequest request, TransactionType type, BigDecimal
            balance, Account account) {
        return AccountTransaction.builder()
                .title(request.getSender())
                .type(type)
                .amount(request.getAmount())
                .balance(balance)
                .sender(request.getSender())
                .receiver(request.getReceiver())
                .memo("")
                .createdAt(LocalDateTime.now())
                .account(account)
                .build();
    }

    private AccountTransaction findAccountTransactionById(Long transactionId, String message) {
        return accountTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new WekidsException(ErrorCode.TRANSACTION_NOT_FOUND, message + transactionId));
    }

    private void validateTransaction(TransactionRequest transactionRequest, Account parentAccount, Account
            childAccount) {
        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new WekidsException(ErrorCode.INVALID_TRANSACTION_AMOUNT, "거래하려는 금액 " + transactionRequest.getAmount());
        }

        if (parentAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new WekidsException(ErrorCode.INVALID_TRANSACTION_AMOUNT,
                    "부모의 잔액" + parentAccount.getBalance() + " 거래하려는 금액 " + transactionRequest.getAmount());
        }

        if (parentAccount.getAccountNumber() == null || childAccount.getAccountNumber() == null) {
            throw new WekidsException(ErrorCode.INVALID_ACCOUNT_NUMBER,
                    "부모 AccountNum:" + parentAccount + " 자식 AccountNum" + childAccount);
        }

        if (parentAccount.getState() != AccountState.ACTIVE) {
            throw new WekidsException(ErrorCode.ACCOUNT_NOT_ACTIVE,
                    "부모 계좌 상태가 활성 상태가 아닙니다. 상태: " + parentAccount.getState());
        }
        if (childAccount.getState() != AccountState.ACTIVE) {
            throw new WekidsException(ErrorCode.ACCOUNT_NOT_ACTIVE,
                    "자식 계좌 상태가 활성 상태가 아닙니다. 상태: " + childAccount.getState());
        }
        if (parentAccount.getAccountNumber().equals(childAccount.getAccountNumber())) {
            throw new WekidsException(ErrorCode.INVALID_ACCOUNT_NUMBER,
                    "부모 계좌와 자식 계좌가 동일할 수 없습니다.");
        }
    }
}

