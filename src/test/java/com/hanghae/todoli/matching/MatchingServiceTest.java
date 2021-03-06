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
    @DisplayName("????????? ??????")
    class searchMemberTest {

        @Test
        @DisplayName("????????? ?????? ?????? - ?????? ?????? false ??????")
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
        @DisplayName("????????? ?????? ?????? - ?????? ?????? true ??????(1,2?????? ?????????)")
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
        @DisplayName("????????? ?????? ?????? - ?????? ?????? true ??????(1,2?????? ????????????)")
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
        @DisplayName("????????? ?????? ?????? - ??????????????? .??????")
        void searchMemberFail1() {
            //given
            String inputUsername = "test2@navercom";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ??????????????? @??????")
        void searchMemberFail2() {
            //given
            String inputUsername = "test2naver.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????????@,.??????")
        void searchMemberFail3() {
            //given
            String inputUsername = "test2navercom";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????????@?????? ??????")
        void searchMemberFail4() {
            //given
            String inputUsername = "@naver.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????????@,.?????? ??????")
        void searchMemberFail5() {
            //given
            String inputUsername = "test2@.com";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????????. ??? ??????")
        void searchMemberFail6() {
            //given
            String inputUsername = "test2@naver.";

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????? ?????? ?????? ??????")
        void searchMemberFail7() {
            //given
            String inputUsername = "test2@naver.com";
            Member member = new Member();
            UserDetailsImpl wrongUser = new UserDetailsImpl(member);
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, wrongUser));

            //then
            Assertions.assertEquals("?????? ?????? ????????? ?????? ??? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ????????? ?????? ????????????")
        void searchMemberFail8() {
            //given
            String inputUsername = "test2@naver.com";
            given(memberRepository.findById(userDetails.getMember().getId())).willReturn(Optional.ofNullable(existMember));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.searchMember(inputUsername, userDetails));

            //then
            Assertions.assertEquals("????????? ????????? ?????? ??? ????????????.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("????????? ??????")
    class inviteMatchingTest {

        @Test
        @DisplayName("????????? ?????? ??????")
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
                    userDetails.getMember().getNickname() + "?????? ?????????????????????????", result.getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ????????? ?????? ???")
        void inviteMatchingFail1() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("????????? ?????? ??? ?????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????? ??????")
        void inviteMatchingFail2() {
            //given
            Long memberId = existMember2.getId();
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("????????? ????????? ?????? ??? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ???????????? ?????????")
        void inviteMatchingFail3() {
            //given
            Long memberId = existMember2.getId();
            existMember2.setMatchingState(true);
            given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(existMember2));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.inviteMatching(memberId, userDetails));
            //then
            Assertions.assertEquals("???????????? ?????? ?????? ????????????.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("?????? ??????")
    class cancelMatchingTest {

        @Test
        @DisplayName("?????? ?????? ??????")
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
        @DisplayName("?????? ?????? ?????? - ?????? ?????? ????????? ??????")
        void cancelMatchingFail1() {
            //given
            Long memberId = existMember2.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("????????? ?????? ????????? ?????? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("?????? ?????? ?????? - ??? ????????? ??????")
        void cancelMatchingFail2() {
            //given
            Long memberId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.cancelMatching(memberId, userDetails));

            //then
            Assertions.assertEquals("?????? ?????? ????????? ?????? ??? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("?????? ?????? ?????? - ???????????? ??????")
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
            Assertions.assertEquals("????????? ?????? ????????? ?????? ??? ????????????.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("?????? ?????? ?????? - ??????????????? ??????")
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
            Assertions.assertEquals("????????? ???????????? ????????????.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("?????? ??????")
    class acceptMatchingTest {

        @Test
        @DisplayName("?????? ?????? ??????")
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
        @DisplayName("?????? ?????? ?????? - ?????? ?????? ???")
        void acceptMatchingFail1() {
            //given
            Long senderId = existMember2.getId();
            userDetails.getMember().setMatchingState(true);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("????????? ?????? ??? ?????????.",exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("?????? ?????? ?????? - ?????? ????????? ??????")
        void acceptMatchingFail2() {
            //given
            Long senderId = existMember2.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("?????? ?????? ????????? ?????? ??? ????????????.",exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("?????? ?????? ?????? - ?????? ?????? ?????? ??????")
        void acceptMatchingFail3() {
            //given
            Long senderId = existMember2.getId();
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> matchingService.acceptMatching(senderId, userDetails));

            //then
            Assertions.assertEquals("????????? ?????? ?????? ????????? ?????? ??? ????????????.",exception.getErrorCode().getMessage());
        }
    }
}