package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.CharResponseDto;
import com.hanghae.todoli.dto.EquipItemListDto;
import com.hanghae.todoli.models.*;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.repository.ItemRepository;
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
    private final ItemRepository itemRepository;


    //캐릭터 상태 조회
    @Transactional
    public CharResponseDto getCharState(UserDetailsImpl userDetails) {
        Member m = userDetails.getMember();
        Character c = m.getCharacter();

        //캐릭터가 장착한 아이템에서 필요한 정보 가져오기
        List<EquipItemListDto> itemList = getListDtos(c);

        return CharResponseDto.builder()
                .matchingState(m.getMatchingState())
                .level(c.getLevel())
                .hp(c.getHp())
                .maxHp(c.getMaxHp())
                .exp(c.getExp())
                .maxExp(c.getMaxExp())
                .money(c.getMoney())
                .charImg(c.getCharImg())
                .listDto(itemList)
                .build();
    }


    //상대방 캐릭터 상태 조회
    @Transactional
    public CharResponseDto.PartnerDto getPartnerState(UserDetailsImpl userDetails) {
        Long userId = userDetails.getMember().getId();
        Matching matching = matchingRepository.getMatching(userId).orElseThrow(
                () -> new IllegalArgumentException("매칭된 상대가 존재하지 않습니다.")
        );

        //파트너 아이디 구하기
        Long partnerId = userId.equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

        Member partner = memberRepository.findById(partnerId).orElseThrow(
                ()-> new IllegalArgumentException("파트너가 존재하지 않습니다.")
        );

        Character c = partner.getCharacter();
        List<EquipItemListDto> memberItems = getListDtos(c);

        return CharResponseDto.PartnerDto.builder()
                .memberId(partner.getId())
                .nickname(partner.getNickname())
                .charImg(partner.getCharacter().getCharImg())
                .listDto(memberItems)
                .build();
    }







    //캐릭터가 장착한 아이템에서 필요한 정보 가져오기
    private List<EquipItemListDto> getListDtos(Character c) {
        Long hairId = c.getEquipItem().getHairId();
        Long accessoryId = c.getEquipItem().getAccessoryId();
        Long clothId = c.getEquipItem().getClothId();
        Long hatId = c.getEquipItem().getHatId();

        List<EquipItemListDto>itemList = new ArrayList<>();

        if(hairId !=null){
            EquipItemListDto hair = addItem(hairId);
            itemList.add(hair);
        }
        if(accessoryId !=null){
            EquipItemListDto accessory = addItem(accessoryId);
            itemList.add(accessory);
        }
        if(clothId !=null){
            EquipItemListDto cloth = addItem(clothId);
            itemList.add(cloth);
        }
        if(hatId !=null){
            EquipItemListDto hat = addItem(hatId);
            itemList.add(hat);
        }

        return itemList;
    }

    //장착된 아이템에서 원하는 정보만 가져오기
    public EquipItemListDto addItem(Long itemId) {
        EquipItemListDto itemListDto = new EquipItemListDto();
        if (itemId != null) {
            Item item = itemRepository.findById(itemId).orElse(null);
            itemListDto.setItemId(itemId);
            itemListDto.setEquipImg(item.getEquipImg());    //Objects.requireNonNull(item).getEquipImg()
            itemListDto.setCategory(item.getCategory());
        }
        return itemListDto;
    }
}
