package com.hanghae.todoli.alarm;

import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.ThumbnailDto;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    //전체 알람조회
    public List<AlarmResponseDto> getAlarms(UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();  //a

        List<Alarm> alarms = alarmRepository.findAllByMemberId(id);  // a의 알람전체

        List<AlarmResponseDto> alarmList = new ArrayList<>();

        for (Alarm alarm : alarms) {
            Long senderId = alarm.getSenderId();
            Member sender = memberRepository.findById(senderId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

            List<ThumbnailDto> senderEquipItems = getThumbnailDtos(sender);

            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .alarmState(alarm.getAlarmState())
                    .message(alarm.getMessage())
                    .alarmDate(alarm.getAlarmDate())
                    .alarmType(alarm.getAlarmType())
                    .senderId(alarm.getSenderId())
                    .thumbnailCharImg(new CharacterImg().getThumbnailCharImg())
                    .senderEquipItems(senderEquipItems)
                    .build();
            alarmList.add(alarmResponseDto);
        }
        return alarmList;
    }

    //알람 삭제
    @Transactional
    public void deleteAlarms(UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        alarmRepository.deleteAllByMemberId(id);
    }

    private List<ThumbnailDto> getThumbnailDtos(Member Info) {
        List<ThumbnailDto> myEquipItemList = new ArrayList<>();
        EquipItem myEquipItem = Info.getCharacter().getEquipItem();
        Long hairId = myEquipItem.getHairId();
        Long clothId = myEquipItem.getClothId();
        Long accessoryId = myEquipItem.getAccessoryId();
        if (hairId != null) {
            Item hair = itemRepository.findById(hairId).orElse(null);
            ThumbnailDto thumbnailDto1 = new ThumbnailDto(hair.getId(), hair.getThumbnailImg(), hair.getCategory());
            myEquipItemList.add(thumbnailDto1);
        }
        if (clothId != null) {
            Item cloth = itemRepository.findById(clothId).orElse(null);
            ThumbnailDto thumbnailDto2 = new ThumbnailDto(cloth.getId(), cloth.getThumbnailImg(), cloth.getCategory());
            myEquipItemList.add(thumbnailDto2);
        }
        if (accessoryId != null) {
            Item accessory = itemRepository.findById(accessoryId).orElse(null);
            ThumbnailDto thumbnailDto3 = new ThumbnailDto(accessory.getId(), accessory.getThumbnailImg(), accessory.getCategory());
            myEquipItemList.add(thumbnailDto3);
        }
        return myEquipItemList;
    }
}
