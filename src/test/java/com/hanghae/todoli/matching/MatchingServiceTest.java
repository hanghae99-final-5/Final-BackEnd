package com.hanghae.todoli.matching;

import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.alarm.AlarmType;
import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.Dto.ThumbnailDto;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.character.repository.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.matching.dto.MatchingResponseDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private ThumbnailDtoList thumbnailDtoList;

    MatchingService matchingService;

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
            "test2@naver.com",
            "test2",
            "password2",
            false,
            character
    );
    Member existMember3 = new Member(
            "test3@naver.com",
            "test3",
            "password3",
            false,
            character
    );

    UserDetailsImpl userDetails = new UserDetailsImpl(existMember);

    Matching matching = new Matching(
            1L, 2L
    );

    @BeforeEach
    void beforeEach() {
        this.matchingService = new MatchingService(
                memberRepository,
                alarmRepository,
                matchingRepository,
                todoRepository,
                thumbnailDtoList,
                characterRepository

        );

        existMember.setId(1L);
        existMember2.setId(2L);
        existMember3.setId(3L);
    }

    @Nested
    @DisplayName("상대방 찾기")
    class searchMemberTest {

        @Test
        @DisplayName("상대방 찾기 성공 - 타겟 매칭 false 일때")
        void searchMember() {
            //given
            String inputUsername = "test2@naver.com";
            Long id = userDetails.getMember().getId();

            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findByUsername(inputUsername)).willReturn(Optional.ofNullable(existMember2));


            List<ThumbnailDto> existMember2Items = thumbnailDtoList.getThumbnailDtos(existMember2);
            //when
            MatchingResponseDto matchingResponseDto = matchingService.searchMember(inputUsername, userDetails);

            //then
            Assertions.assertEquals(userDetails.getMember().getMatchingState(), matchingResponseDto.getMyMatchingState());
            Assertions.assertEquals(existMember2.getId(), matchingResponseDto.getMemberId());
            Assertions.assertEquals(existMember2.getNickname(), matchingResponseDto.getNickname());
            Assertions.assertEquals(existMember2.getMatchingState(), matchingResponseDto.getPartnerMatchingState());
            Assertions.assertEquals("", matchingResponseDto.getSearchedUserPartner());
            Assertions.assertEquals(new CharacterImg().getThumbnailCharImg(), matchingResponseDto.getThumbnailCharImg());
            Assertions.assertEquals(existMember2Items, matchingResponseDto.getEquipItems());
        }

        @Test
        @DisplayName("상대방 찾기 성공 - 타겟 매칭 true 일때(1,2서로 매칭중)")
        void searchMember2() {
            //given
            String inputUsername = "test2@naver.com";
            Long id = userDetails.getMember().getId();
            existMember2.setMatchingState(true);

            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findByUsername(inputUsername)).willReturn(Optional.ofNullable(existMember2));
            given(matchingRepository.getMatching(existMember2.getId())).willReturn(Optional.ofNullable(matching));

            List<ThumbnailDto> existMember2Items = thumbnailDtoList.getThumbnailDtos(existMember2);
            //when
            MatchingResponseDto matchingResponseDto = matchingService.searchMember(inputUsername, userDetails);

            //then
            Assertions.assertEquals(userDetails.getMember().getMatchingState(), matchingResponseDto.getMyMatchingState());
            Assertions.assertEquals(existMember2.getId(), matchingResponseDto.getMemberId());
            Assertions.assertEquals(existMember2.getNickname(), matchingResponseDto.getNickname());
            Assertions.assertEquals(existMember2.getMatchingState(), matchingResponseDto.getPartnerMatchingState());
            Assertions.assertEquals("test@naver.com", matchingResponseDto.getSearchedUserPartner());
            Assertions.assertEquals(new CharacterImg().getThumbnailCharImg(), matchingResponseDto.getThumbnailCharImg());
            Assertions.assertEquals(existMember2Items, matchingResponseDto.getEquipItems());
        }

        @Test
        @DisplayName("상대방 찾기 성공 - 타겟 매칭 true 일때(1,2서로 매칭아님)")
        void searchMember3() {
            //given
            String inputUsername = "test2@naver.com";
            Long id = userDetails.getMember().getId();
            existMember2.setMatchingState(true);
            existMember3.setMatchingState(true);
            matching.setRequesterId(3L);

            given(memberRepository.findById(id)).willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findById(existMember3.getId())).willReturn(Optional.ofNullable(existMember3));
            given(memberRepository.findByUsername(inputUsername)).willReturn(Optional.ofNullable(existMember2));
            given(matchingRepository.getMatching(existMember2.getId())).willReturn(Optional.ofNullable(matching));


            List<ThumbnailDto> existMember2Items = thumbnailDtoList.getThumbnailDtos(existMember2);
            //when
            MatchingResponseDto matchingResponseDto = matchingService.searchMember(inputUsername, userDetails);

            //then
            Assertions.assertEquals(userDetails.getMember().getMatchingState(), matchingResponseDto.getMyMatchingState());
            Assertions.assertEquals(existMember2.getId(), matchingResponseDto.getMemberId());
            Assertions.assertEquals(existMember2.getNickname(), matchingResponseDto.getNickname());
            Assertions.assertEquals(existMember2.getMatchingState(), matchingResponseDto.getPartnerMatchingState());
            Assertions.assertEquals("test3@naver.com", matchingResponseDto.getSearchedUserPartner());
            Assertions.assertEquals(new CharacterImg().getThumbnailCharImg(), matchingResponseDto.getThumbnailCharImg());
            Assertions.assertEquals(existMember2Items, matchingResponseDto.getEquipItems());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식 .없음")
        void searchMemberFail1() {
            //given
            String inputUsername = "test2@navercom";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식 @없음")
        void searchMemberFail2() {
            //given
            String inputUsername = "test2naver.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식@,.없음")
        void searchMemberFail3() {
            //given
            String inputUsername = "test2navercom";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식@앞에 없음")
        void searchMemberFail4() {
            //given
            String inputUsername = "@naver.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식@,.사이 없음")
        void searchMemberFail5() {
            //given
            String inputUsername = "test2@.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 이메일형식. 뒤 없음")
        void searchMemberFail6() {
            //given
            String inputUsername = "test2@naver.";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("이메일 형식이 아닙니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 로그인한 유저 정보 없음")
        void searchMemberFail7() {
            //given
            String inputUsername = "test2@naver.com";
            Member member = new Member();
            UserDetailsImpl wrongUser = new UserDetailsImpl(member);
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, wrongUser));

            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 찾기 실패 - 검색한 유저 정보없음")
        void searchMemberFail8() {
            //given
            String inputUsername = "test2@naver.com";
            given(memberRepository.findById(userDetails.getMember().getId())).willReturn(Optional.ofNullable(existMember));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("상대방 유저를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("상대방 초대")
    class inviteMatchingTest {

        @Test
        @DisplayName("상대방 초대 성공")
        void inviteMatching() {
            //given
            Long memberId = existMember2.getId();
            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(existMember2));

            //when
            Alarm result = matchingService.inviteMatching(memberId, userDetails);
            //then
            Assertions.assertEquals(LocalDate.now(), result.getAlarmDate());
            Assertions.assertEquals(0, result.getAlarmState());
            Assertions.assertEquals(AlarmType.ACCEPTANCE, result.getAlarmType());
            Assertions.assertEquals(existMember2, result.getMember());
            Assertions.assertEquals(userDetails.getMember().getId(), result.getSenderId());
            Assertions.assertEquals(
                    userDetails.getMember().getNickname() + "님과 함께하시겠습니까?", result.getMessage());
        }

        @Test
        @DisplayName("상대방 초대 실패 - 자신이 매칭 중")
        void inviteMatchingFail1() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("자신이 매칭 중 입니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 초대 실패 - 상대방이 없음")
        void inviteMatchingFail2() {
            //given
            Long memberId = existMember2.getId();
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("상대방 유저를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 초대 실패 - 상대방이 매칭중")
        void inviteMatchingFail3() {
            //given
            Long memberId = existMember2.getId();
            existMember2.setMatchingState(true);
            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(existMember2));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("상대방이 이미 매칭 중입니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("매칭 취소")
    class cancelMatchingTest {

        @Test
        @DisplayName("매칭 취소 성공")
        void cancelMatching() {
            //given
            existMember.setMatchingState(true);
            existMember2.setMatchingState(true);
            Long memberId = existMember2.getId();
            matching.setId(1L);
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.ofNullable(existMember2));
            given(matchingRepository.getMatching(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(matching));


            //when
            matchingService.cancelMatching(memberId, userDetails);


            //then
            Assertions.assertEquals(false, existMember.getMatchingState());
            Assertions.assertEquals(false, existMember2.getMatchingState());
            verify(matchingRepository, times(1)).delete(matching);
            verify(todoRepository, times(1))
                    .deleteAllByWriterIdAndTodoType(userDetails.getMember().getId(), 2);
            verify(todoRepository, times(1))
                    .deleteAllByWriterIdAndTodoType(memberId, 2);
        }

        @Test
        @DisplayName("매칭 취소 실패 - 내가 매칭 상태가 아님")
        void cancelMatchingFail1() {
            //given
            Long memberId = existMember2.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("자신이 매칭되어있지 않습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("매칭 취소 실패 - 내 정보가 없음")
        void cancelMatchingFail2() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("매칭 취소 실패 - 상대방이 없음")
        void cancelMatchingFail3() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("파트너 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("매칭 취소 실패 - 매칭정보가 없음")
        void cancelMatchingFail4() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findById(memberId))
                    .willReturn(Optional.ofNullable(existMember2));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("매칭이 되어있지 않습니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("매칭 수락")
    class acceptMatchingTest {

        @Test
        @DisplayName("매칭 수락 성공")
        void acceptMatching() {
            //given
            Long senderId = existMember2.getId();
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));
            given(memberRepository.findById(senderId))
                    .willReturn(Optional.ofNullable(existMember2));

            //when
            matchingService.acceptMatching(senderId,userDetails);

            //then
            Assertions.assertEquals(true,existMember.getMatchingState());
            Assertions.assertEquals(true,existMember2.getMatchingState());
            verify(matchingRepository, times(1)).save(any(Matching.class));
            verify(alarmRepository, times(1)).findAllByAlarm(userDetails.getMember().getId());
        }
        @Test
        @DisplayName("매칭 수락 실패 - 자신 매칭 중")
        void acceptMatchingFail1() {
            //given
            Long senderId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("자신이 매칭 중 입니다.",exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("매칭 수락 실패 - 자신 정보가 없음")
        void acceptMatchingFail2() {
            //given
            Long senderId = existMember2.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.",exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("매칭 수락 실패 - 요청 보낸 유저 없음")
        void acceptMatchingFail3() {
            //given
            Long senderId = existMember2.getId();
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("요청을 보낸 유저 정보를 찾을 수 없습니다.",exception.getErrorCode().getMessage());
        }
    }
}