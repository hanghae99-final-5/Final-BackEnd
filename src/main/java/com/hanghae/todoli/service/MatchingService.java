package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.ItemListDto;
import com.hanghae.todoli.dto.MatchingResponseDto;
import com.hanghae.todoli.models.*;
import com.hanghae.todoli.repository.*;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final AlarmRepository alarmRepository;
    private final MatchingRepository matchingRepository;
    private final TodoRepository todoRepository;

    List<ItemListDto> itemList = new ArrayList<>();

    //상대방 찾기
    public MatchingResponseDto searchMember(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        EquipItem equipItem = member.getCharacter().getEquipItem();

        Long accessoryId = equipItem.getAccessoryId();
        Long clothId = equipItem.getClothId();
        Long hairId = equipItem.getHairId();
        Long hatId = equipItem.getHatId();

        addItem(accessoryId);
        addItem(clothId);
        addItem(hairId);
        addItem(hatId);

        return MatchingResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .matchingState(member.getMatchingState())
                .charImg(member.getCharacter().getCharImg())
                .equipItems(itemList)
                .build();
    }

    //상대방 초대
    public void inviteMatching(Long memberId, UserDetailsImpl userDetails) {
        Alarm alarm = new Alarm();
        //상대방 찾기
        Member targetMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("상대방이 존재하지 않습니다.")
        );
        if (targetMember.getMatchingState()) {
            throw new IllegalArgumentException("상대방이 이미 매칭 중입니다.");
        }

        //현재 날짜 출력
        Date now = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        alarm.setAlarmDate(date.format(now));
        alarm.setMember(targetMember);
        alarm.setSenderId(userDetails.getMember().getId());
        alarm.setMessage(userDetails.getMember().getNickname() + "님과 함께하시겠습니까?");

        alarmRepository.save(alarm);
    }

    //매칭 취소
    public void cancelMatching(Long memberId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        //상대방 찾기
        Member targetMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("상대방이 존재하지 않습니다.")
        );
        Matching matching = matchingRepository.getMatching(member.getId());
        //매칭 정보 삭제
        matchingRepository.delete(matching);
        //멤버의 매칭 상태 변경
        member.changeMatchingState(member);
        targetMember.changeMatchingState(targetMember);
        //멤버의 투두리스트 삭제
        todoRepository.deleteAllByWriterId(member.getId());
        todoRepository.deleteAllByWriterId(targetMember.getId());
    }

    //매칭 수락
   public void acceptMatching(Long senderId, UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Member sender = memberRepository.findById(senderId).orElseThrow(
                () -> new IllegalArgumentException("상대방이 없습니다.")
        );

        //멤버의 매칭 상태 변경
        member.changeMatchingState(member);
        sender.changeMatchingState(sender);

        //매칭에 매칭 정보 저장
        Matching matching = new Matching(senderId,member.getId());
        matchingRepository.save(matching);
    }

    //아이템 리스트dto에 추가
    public void addItem(Long id) {
        ItemListDto itemListDto = new ItemListDto();
        if (id != null) {
            Item item = itemRepository.findById(id).orElse(null);
            itemListDto.setItemId(id);
            itemListDto.setEquipImg(item.getEquipImg());
            itemList.add(itemListDto);
        }
    }
}
