package com.wekids.backend.account.repository;

import com.wekids.backend.account.domain.Account;
import com.wekids.backend.account.domain.enums.AccountDesignType;
import com.wekids.backend.account.domain.enums.AccountState;
import com.wekids.backend.account.dto.response.AccountResponse;
import com.wekids.backend.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @Test
    void test() {
        List<Account> all = accountRepository.findAll();
        System.out.println("all = " + all);
    }

    @Test
    void 계좌_가져오기(){
        AccountResponse response = accountService.getAccount(1L);
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("김철수");
        assertThat(response.getAmount()).isEqualTo("100000.00");
        assertThat(response.getState()).isEqualTo(AccountState.ACTIVE);
        assertThat(response.getDesign_type()).isEqualTo(AccountDesignType.WEBEE);
        assertThat(response.getAccount_number()).isEqualTo("123-456-7890");

    }
}