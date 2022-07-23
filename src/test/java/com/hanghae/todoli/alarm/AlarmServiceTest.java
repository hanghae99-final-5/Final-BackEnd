package com.hanghae.todoli.alarm;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingService;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@Transactional
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ThumbnailDtoList thumbnailDtoList;
    AlarmService alarmService;

    EquipItem equipItem = new EquipItem(
            1L,
            null,
            null,
            null
    );
    Character character = new Character(
            1L,
            new CharacterImg().getCharImg(),
            100,
            0,
            100,
            0,
            1,
            10000,
            null,
            equipItem
    );
    Member existMember = new Member(
            "test@naver.com",
            "test",
            "password",
            false,
            character
    );
    Member existMember2 = new Member(
            "test@naver.com",
            "test",
            "password",
            false,
            character
    );
    LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    UserDetailsImpl userDetails = new UserDetailsImpl(existMember);


    @BeforeEach
    void beforeEach() {
        this.alarmService = new AlarmService(
                alarmRepository,
                memberRepository,
                thumbnailDtoList
        );

        existMember.setId(1L);
        existMember2.setId(2L);
    }

    @Nested
    @DisplayName("전체 알람조회")
    class getAlarmsTest {

        @Test
        @DisplayName("전체 알람조회 성공")
        void getAlarms() {
            //given
            Long id = userDetails.getMember().getId();
            List<AlarmResponseDto> alarmList = new ArrayList<>();

            Alarm alarm1 = new Alarm(
                    1L, "test1", now, 2L,
                    0L, 1L, AlarmType.AUTHENTICATION
                    , existMember
            );
            Alarm alarm2 = new Alarm(
                    2L, "test2", now, 2L,
                    0L, 2L, AlarmType.AUTHENTICATION
                    , existMember
            );
            given(memberRepository.findById(alarm1.getSenderId()))
                    .willReturn(Optional.ofNullable(existMember2));

            //when
            List<AlarmResponseDto> result = alarmService.getAlarms(userDetails);

            //then
        }
    }


    @Test
    void deleteAlarms() {
    }
}