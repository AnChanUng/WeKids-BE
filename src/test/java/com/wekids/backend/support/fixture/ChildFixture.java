package com.wekids.backend.support.fixture;

import com.wekids.backend.member.domain.Child;
import com.wekids.backend.member.domain.enums.CardState;
import com.wekids.backend.member.domain.enums.MemberState;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class ChildFixture {

    private Long id;
    private String name = "조예은"; // 더미 데이터 이름
    private String email = "1234"; // 더미 데이터 이메일
    private String simplePassword = "12345"; // 더미 데이터 비밀번호
    private String phone = "1"; // 예시 전화번호
    private LocalDate birthday = LocalDate.of(1998, 5, 1); // 예시 생일
    private String profile = "https://image.com/virtual.png"; // 프로필 이미지 URL
    private MemberState state = MemberState.ACTIVE; // 상태
    private LocalDateTime inactiveDate = null; // 비활성화 날짜는 null로 설정
    private CardState cardState = CardState.NONE; // 카드 상태
    private Long bankMemberId = null; // 은행 회원 ID는 null로 설정

    public Child build() {
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

