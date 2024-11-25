package com.wekids.backend.support.fixture;

import com.wekids.backend.member.domain.Child;
import com.wekids.backend.member.domain.enums.CardState;
import com.wekids.backend.member.domain.enums.MemberState;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class ChildFixture {
    @Builder.Default
    private Long id = 1L;
    @Builder.Default
    private String name = "조예은";
    @Builder.Default
    private String email = "test@test.com";
    @Builder.Default
    private String simplePassword = "123456";
    @Builder.Default
    private String phone = "010-1111-1111";
    @Builder.Default
    private LocalDate birthday = LocalDate.of(1998, 5, 1);
    @Builder.Default
    private String profile = "https://image.com/virtual.png";
    @Builder.Default
    private MemberState state = MemberState.ACTIVE;
    @Builder.Default
    private LocalDateTime inactiveDate = null;
    @Builder.Default
    private CardState cardState = CardState.NONE;
    @Builder.Default
    private Long bankMemberId = null;

    public Child from() {
        return Child.builder()
                .id(id)
                .name(name)
                .email(email)
                .simplePassword(simplePassword)
                .phone(phone)
                .birthday(birthday)
                .profile(profile)
                .state(state)
                .cardState(cardState)
                .bankMemberId(bankMemberId)
                .inactiveDate(inactiveDate)
                .build();
    }
}
