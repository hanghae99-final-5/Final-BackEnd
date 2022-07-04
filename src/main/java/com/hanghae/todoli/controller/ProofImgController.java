package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.ProofImgRequestDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.ProofImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProofImgController {

    /**
     * 인증 사진 등록
     * - 사진 등록시 인증 날짜 = 종료일 + 3
     */

    private final ProofImgService imgService;

    @PatchMapping("/proofimgs/{id}")
    public void proofImgRegister(@PathVariable Long id, ProofImgRequestDto imgRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        imgService.imgRegister(id, imgRequestDto, userDetails);
    }
}
