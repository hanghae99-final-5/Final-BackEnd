package com.hanghae.todoli.character;

import com.hanghae.todoli.character.Dto.CharResponseDto;
import com.hanghae.todoli.character.Dto.FooterResponseDto;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CharacterController {

    private final CharacterService characterService;

    //캐릭터 상태 조회
    @GetMapping("/api/characters")
    public CharResponseDto getCharState(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return characterService.getCharState(userDetails);
    }

    //상대방 캐릭터 상태
    @GetMapping("/api/characters/partners")
    public CharResponseDto getPartnerState(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return characterService.getPartnerState(userDetails);
    }

    //푸터용 자신, 상대방 캐릭터
    @GetMapping("/api/characters/footer")
    public FooterResponseDto getCharacterInFooter(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return characterService.getCharacterInFooter(userDetails);
    }
}
