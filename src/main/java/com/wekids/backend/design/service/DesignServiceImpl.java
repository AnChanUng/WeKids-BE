package com.wekids.backend.design.service;

import com.wekids.backend.design.domain.Design;
import com.wekids.backend.design.domain.enums.CharacterType;
import com.wekids.backend.design.domain.enums.ColorType;
import com.wekids.backend.design.dto.request.DesignCreateRequest;
import com.wekids.backend.design.dto.response.DesignResponse;
import com.wekids.backend.design.repository.DesignRepository;
import com.wekids.backend.exception.ErrorCode;
import com.wekids.backend.exception.WekidsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class DesignServiceImpl implements DesignService {

    private final DesignRepository designRepository;

    @Override
    public DesignResponse showDesign(Long memberId) {
        Design design = findDesignByMemberId(memberId);
        return DesignResponse.of(design.getColor().name(), design.getCharacter().name());
    }

    @Override
    @Transactional
    public void createDesign(Long memberId, DesignCreateRequest request) {

        ColorType color = ColorType.valueOf(request.getColor());
        CharacterType character = CharacterType.valueOf(request.getCharacter());

        Design newDesign = Design.create(memberId, color, character);
        designRepository.save(newDesign);
    }

    private Design findDesignByMemberId(Long memberId) {
        return designRepository.findById(memberId)
                .orElseThrow(() -> new WekidsException(ErrorCode.DESIGN_NOT_FOUND, "디자인을 저장 할 memberId는 " + memberId));
    }

}