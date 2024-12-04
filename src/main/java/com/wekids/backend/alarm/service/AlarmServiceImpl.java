package com.wekids.backend.alarm.service;

import com.wekids.backend.alarm.domain.Alarm;
import com.wekids.backend.alarm.dto.response.AlarmGetResponse;
import com.wekids.backend.alarm.repository.AlarmRepository;
import com.wekids.backend.exception.ErrorCode;
import com.wekids.backend.exception.WekidsException;
import com.wekids.backend.member.domain.Member;
import com.wekids.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AlarmServiceImpl implements AlarmService{
    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<AlarmGetResponse> getAlarmList(Long memberId) {
        Member member = getMember(memberId);
        List<Alarm> alarms = alarmRepository.findAllByMember(member);
        return AlarmGetResponse.from(alarms);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new WekidsException(ErrorCode.MEMBER_NOT_FOUND, "회원 아이디: " + memberId));
    }
}
