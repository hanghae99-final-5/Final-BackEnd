package com.hanghae.todoli.alarm;

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

    //전체 알람조회
    public List<AlarmResponseDto> getAlarms(UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        List<Alarm> alarms = alarmRepository.findAllByMemberId(id);

        List<AlarmResponseDto> alarmList = new ArrayList<>();
        for (Alarm alarm : alarms) {
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                    .alarmId(alarm.getId())
                    .message(alarm.getMessage())
                    .alarmDate(alarm.getAlarmDate())
                    .senderId(alarm.getSenderId())
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
}
