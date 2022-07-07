package com.hanghae.todoli.character;

import com.hanghae.todoli.equipitem.EquipItemDto;
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


    //캐릭터 상태 조회
    //@Transactional
    public CharResponseDto getCharState(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("자신의 아이디가 잘못되었습니다.")
        );
        return getCharResponseDto(member);
    }


    //상대방 캐릭터 상태 조회
    //@Transactional
    public CharResponseDto getPartnerState(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Matching matching = matchingRepository.getMatching(userId).orElseThrow(
                () -> new IllegalArgumentException("매칭된 상대가 존재하지 않습니다.")
        );

        //파트너 아이디 구하기
        Long partnerId = userId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();
        Member partner = memberRepository.findById(partnerId).orElseThrow(
                () -> new IllegalArgumentException("파트너가 존재하지 않습니다.")
        );

        return getCharResponseDto(partner);
    }

    private CharResponseDto getCharResponseDto(Member partner) {
        Character c = partner.getCharacter();
        List<EquipItemDto> memberItems = getListDtos(c);

        return CharResponseDto.builder()
                .matchingState(partner.getMatchingState())
                .level(c.getLevel())
                .hp(c.getHp())
                .maxHp(c.getMaxHp())
                .exp(c.getExp())
                .maxExp(c.getMaxExp())
                .money(c.getMoney())
                .charImg(c.getCharImg())
                .equipItems(memberItems)
                .build();
    }


    //캐릭터가 장착한 아이템에서 필요한 정보 가져오기
    private List<EquipItemDto> getListDtos(Character c) {
        Long hairId = c.getEquipItem().getHairId();
        Long accessoryId = c.getEquipItem().getAccessoryId();
        Long clothId = c.getEquipItem().getClothId();

        List<EquipItemDto> itemList = new ArrayList<>();

        if (hairId != null) {
            EquipItemDto hair = addItem(hairId);
            itemList.add(hair);
        }
        if (accessoryId != null) {
            EquipItemDto accessory = addItem(accessoryId);
            itemList.add(accessory);
        }
        if (clothId != null) {
            EquipItemDto cloth = addItem(clothId);
            itemList.add(cloth);
        }
        return itemList;
    }

    //장착된 아이템에서 원하는 정보만 가져오기
    public EquipItemDto addItem(Long itemId) {
        EquipItemDto itemListDto = new EquipItemDto();
        if (itemId != null) {
            Item item = itemRepository.findById(itemId).orElse(null);
            itemListDto.setItemId(itemId);
            itemListDto.setEquipImg(item.getEquipImg());    //Objects.requireNonNull(item).getEquipImg()
            itemListDto.setCategory(item.getCategory());
        }
        return itemListDto;
    }
}
