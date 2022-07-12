package com.hanghae.todoli.alarm;

import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
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

    private final ThumbnailDtoList thumbnailDtoList;

    //전체 알람조회
    public List<AlarmResponseDto> getAlarms(UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();

        List<Alarm> alarms = alarmRepository.findAllByMemberIdOrderByIdDesc(id);  // a의 알람전체

        List<AlarmResponseDto> alarmList = new ArrayList<>();

        for (Alarm alarm : alarms) {
            Long senderId = alarm.getSenderId();
            Member sender = memberRepository.findById(senderId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

            List<ThumbnailDto> senderEquipItems = thumbnailDtoList.getThumbnailDtos(sender);

            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder().alarmId(alarm.getId()).alarmState(alarm.getAlarmState()).message(alarm.getMessage()).alarmDate(alarm.getAlarmDate()).alarmType(alarm.getAlarmType()).senderId(alarm.getSenderId()).thumbnailCharImg(new CharacterImg().getThumbnailCharImg()).senderEquipItems(senderEquipItems).build();
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
}
