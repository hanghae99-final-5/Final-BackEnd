package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.CharResponseDto;
import com.hanghae.todoli.dto.EquipItemListDto;
import com.hanghae.todoli.models.Category;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.models.Inventory;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.repository.MatchingRepository;
import com.hanghae.todoli.repository.MemberRepository;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
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

    //캐릭터 상태 조회
    @Transactional
    public CharResponseDto getCharState(UserDetailsImpl userDetails) {
        Member m = userDetails.getMember();
        Character c = m.getCharacter();

        //로그인한 사용자의 아이템 가져오기
        List<EquipItemListDto> equipItemList = getMemberItems(c);

        //return
        return CharResponseDto.builder()
                .matchingState(m.getMatchingState())
                .level(c.getLevel())
                .hp(c.getHp())
                .maxHp(c.getMaxHp())
                .exp(c.getExp())
                .maxExp(c.getMaxExp())
                .money(c.getMoney())
                .charImg(c.getCharImg())
                .listDto(equipItemList)
                .build();
    }

    //상대방 캐릭터 상태 조회
    @Transactional
    public CharResponseDto.PartnerDto getPartnerState(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Long respondentId = matchingRepository.getRespondentId(userId);
        Member partner = memberRepository.findById(respondentId).orElse(null);

        Character c = partner.getCharacter();
        List<EquipItemListDto> memberItems = getMemberItems(c);

        return CharResponseDto.PartnerDto.builder()
                .memberId(partner.getId())
                .nickname(partner.getNickname())
                .charImg(partner.getCharacter().getCharImg())
                .listDto(memberItems)
                .build();
    }



    //인벤토리를 뒤져서 장착 아이템을 가져오는 함수
    private List<EquipItemListDto> getMemberItems(Character c) {
        List<EquipItemListDto>equipItemList = new ArrayList<>();
        List<Inventory> inventory = c.getInventory();
        for(Inventory i : inventory){
            Long itemId = i.getItem().getId();
            String equipImg = i.getItem().getEquipImg();
            Category category = i.getItem().getCategory();

            EquipItemListDto itemListDto = EquipItemListDto.builder()
                    .itemId(itemId)
                    .equipImg(equipImg)
                    .category(category)
                    .build();
            equipItemList.add(itemListDto);
        }
        return equipItemList;
    }
}
