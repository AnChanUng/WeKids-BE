package com.wekids.backend.account.service;

import com.wekids.backend.account.domain.Account;
import com.wekids.backend.account.dto.response.AccountChildResponse;
import com.wekids.backend.account.dto.response.AccountResponse;
import com.wekids.backend.account.repository.AccountRepository;
import com.wekids.backend.admin.dto.request.AccountStateRequest;
import com.wekids.backend.admin.dto.response.InActiveDateResponse;
import com.wekids.backend.baas.dto.request.AccountGetRequest;
import com.wekids.backend.baas.dto.request.AccountStateChangeRequest;
import com.wekids.backend.baas.dto.response.AccountGetResponse;
import com.wekids.backend.baas.dto.response.AccountStateChangeResponse;
import com.wekids.backend.baas.service.BaasService;
import com.wekids.backend.exception.ErrorCode;
import com.wekids.backend.exception.WekidsException;
import com.wekids.backend.member.domain.Member;
import com.wekids.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final BaasService baasService;

    @Override
    public List<AccountChildResponse> showChildrenAccountList(Long parentId){
        List<Account> childrenAccountList = accountRepository.findAccountsByParentId(parentId);
        return childrenAccountList.stream().map(AccountChildResponse::from).toList();
    }

    @Override
    public List<AccountResponse> showAllAccountList(Long memberId) {
        Member member = getMember(memberId);
        validateMember(memberId, member);
        List<AccountGetResponse> accountGetResponses = baasService.showAccounts(member.getBankMemberId());
        return AccountResponse.from(accountGetResponses);
    }

    private static void validateMember(Long memberId, Member member) {
        if(member.getBankMemberId() == null) throw new WekidsException(ErrorCode.NOT_REGISTERED_BANK_MEMBER, "회원 아이디: " + memberId);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new WekidsException(ErrorCode.MEMBER_NOT_FOUND, "회원 아이디: " + memberId));
    }

    @Override
    @Transactional
    public void updateAccount(Account account) {
        AccountGetResponse accountResponse = baasService.getAccount(AccountGetRequest.of(account.getAccountNumber()));

        if (!account.getState().equals(accountResponse.getState())) {
            account.updateState(accountResponse.getState());
        }

        account.updateBalance(BigDecimal.valueOf(accountResponse.getBalance()));
    }

    @Override
    @Transactional
    public InActiveDateResponse changeAccountState(Long accountId, AccountStateRequest request) {
        Account account = getAccount(accountId);

        Long bankMemberId = findBankMemberIdByAccountId(accountId);

        AccountStateChangeRequest accountStateChangeRequest = AccountStateChangeRequest.of(bankMemberId, account, request.getState());

        AccountStateChangeResponse accountStateChangeResponse = baasService.changeAccountState(accountStateChangeRequest);

        account.updateState(request.getState(), accountStateChangeResponse.getInactiveDate());

        return InActiveDateResponse.from(account.getInactiveDate());
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new WekidsException(ErrorCode.ACCOUNT_NOT_FOUND, "회원 아이디: " + accountId));
    }

    private Long findBankMemberIdByAccountId(Long accountId){
        return memberRepository.findBankMemberIdByAccountId(accountId).orElseThrow(()->
                new WekidsException(ErrorCode.INVALID_INPUT, accountId+"의 고객은 bankMemberId가 존재 하지 않습니다"));
    }
}
