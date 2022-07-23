package com.hanghae.todoli.character;

import com.hanghae.todoli.character.Dto.CharResponseDto;
import com.hanghae.todoli.character.Dto.FooterResponseDto;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CharacterService {

    private final MatchingRepository matchingRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ThumbnailDtoList thumbnailDtoList;

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


    private CharResponseDto getCharResponseDto(Member member) {
        Character c = member.getCharacter();
        List<EquipItemDto> memberItems = getListDtos(c);

        return CharResponseDto.builder()
                .matchingState(member.getMatchingState())
                .level(c.getLevel())
                .hp(c.getHp())
                .maxHp(c.getMaxHp())
                .exp(c.getExp())
                .maxExp(c.getMaxExp())
                .money(c.getMoney())
                .charImg(c.getCharImg())
                .equipItems(memberItems)
                .nickname(member.getNickname())
                .build();
    }


    //캐릭터가 장착한 아이템에서 필요한 정보 가져오기
    private List<EquipItemDto> getListDtos(Character c) {
        Long hairId = c.getEquipItem().getHairId();
        Long accessoryId = c.getEquipItem().getAccessoryId();
        Long clothId = c.getEquipItem().getClothId();

        List<EquipItemDto> itemList = new ArrayList<>();

        if (clothId != null) {
            EquipItemDto cloth = addItem(clothId);
            itemList.add(cloth);
        }
        if (hairId != null) {
            EquipItemDto hair = addItem(hairId);
            itemList.add(hair);
        }
        if (accessoryId != null) {
            EquipItemDto accessory = addItem(accessoryId);
            itemList.add(accessory);
        }
        return itemList;
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

        // TODO : 2022/07/12 refactoring - 종석
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

    //장착된 아이템에서 원하는 정보만 가져오기
    private EquipItemDto addItem(Long itemId) {
        EquipItemDto itemListDto = new EquipItemDto();
        if (itemId != null) {
            Item item = itemRepository.findById(itemId).orElse(null);
            itemListDto.setItemId(itemId);
            itemListDto.setEquipImg(item.getEquipImg());    //Objects.requireNonNull(item).getEquipImg()
            itemListDto.setCategory(item.getCategory());
        }
        return itemListDto;
    }

    //matching 검사
    private Matching getMatching(Long userId) {
        return matchingRepository.getMatching(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));
    }
}
