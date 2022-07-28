package com.hanghae.todoli.character;

import com.hanghae.todoli.character.Dto.CharResponseDto;
import com.hanghae.todoli.character.Dto.FooterResponseDto;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.character.repository.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterService {

    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final ThumbnailDtoList thumbnailDtoList;
    private final CharacterRepository characterRepository;

    //캐릭터 상태 조회
    public CharResponseDto getCharState(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        return getCharResponseDto(member);
    }


    //상대방 캐릭터 상태 조회
    public CharResponseDto getPartnerState(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Matching matching = getMatching(userId);

        //파트너 아이디 구하기
        Long partnerId = userId.equals(matching.getRequesterId())
                ? matching.getRespondentId()
                : matching.getRequesterId();
        Member partner = memberRepository.findById(partnerId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));

        return getCharResponseDto(partner);
    }

    //푸터용 캐릭터 조회
    public FooterResponseDto getCharacterInFooter(UserDetailsImpl userDetails) {
        Long myId = userDetails.getMember().getId();
        Member myInfo = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Matching matching = getMatching(myId);

        Long partnerId = myId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();
        Member partnerInfo = memberRepository.findById(partnerId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));

        List<ThumbnailDto> myEquipItemList = thumbnailDtoList.getThumbnailDtos(myInfo);
        List<ThumbnailDto> partnerEquipItemList = thumbnailDtoList.getThumbnailDtos(partnerInfo);

        return FooterResponseDto.builder()
                .thumbnailCharImg(new CharacterImg().getThumbnailCharImg())
                .myId(myInfo.getId())
                .myNickname(myInfo.getNickname())
                .myEquipItems(myEquipItemList)
                .partnerId(partnerId)
                .partnerNickname(partnerInfo.getNickname())
                .partnerEquipItems(partnerEquipItemList)
                .build();
    }
    private CharResponseDto getCharResponseDto(Member member) {
        Character c = member.getCharacter();
        List<EquipItemDto> charEquipItems = characterRepository.getEquipItems(c.getId());
        charEquipItems.sort(new Comparator<>() {
            @Override
            public int compare(EquipItemDto o1, EquipItemDto o2) {
                char c1 = o1.getCategory().toString().charAt(2);
                char c2 = o2.getCategory().toString().charAt(2);
                if (c1 < c2) {
                    return 1;
                }
                return -1;
            }
        });
        return CharResponseDto.builder()
                .matchingState(member.getMatchingState())
                .level(c.getLevel())
                .hp(c.getHp())
                .maxHp(c.getMaxHp())
                .exp(c.getExp())
                .maxExp(c.getMaxExp())
                .money(c.getMoney())
                .charImg(c.getCharImg())
                .equipItems(charEquipItems)
                .nickname(member.getNickname())
                .build();
    }

    //matching 검사
    private Matching getMatching(Long userId) {
        return matchingRepository.getMatching(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));
    }
}
