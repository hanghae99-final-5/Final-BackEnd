package com.hanghae.todoli.character;

import com.hanghae.todoli.character.Dto.CharResponseDto;
import com.hanghae.todoli.character.Dto.FooterResponseDto;
import com.hanghae.todoli.character.Dto.ThumbnailDtoList;
import com.hanghae.todoli.character.repository.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.matching.Matching;
import com.hanghae.todoli.matching.MatchingRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@Transactional
@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ThumbnailDtoList thumbnailDtoList;
    @Mock
    private CharacterRepository characterRepository;

    CharacterService characterService;

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
        this.characterService = new CharacterService(
                matchingRepository,
                memberRepository,
                thumbnailDtoList,
                characterRepository
        );

        existMember.setId(1L);
        existMember2.setId(2L);
        existMember3.setId(3L);
    }


    @Nested
    @DisplayName("캐릭터 상태 조회")
    class getCharStateTest {

        @Test
        @DisplayName("캐릭터 상태 조회 성공")
        void getCharState() {
            //given
            Long userId = userDetails.getMember().getId();

            given(memberRepository.findById(userId))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CharResponseDto result = characterService.getCharState(userDetails);

            //then
            Assertions.assertEquals(userDetails.getMember().getNickname(),
                    result.getNickname());
            Assertions.assertEquals(userDetails.getMember().getCharacter().getLevel(),
                    result.getLevel());
            Assertions.assertEquals(userDetails.getMember().getCharacter().getMoney(),
                    result.getMoney());
        }

        @Test
        @DisplayName("캐릭터 상태 조회 실패 - 로그인 유저 정보 없음")
        void getCharStateFail1() {
            //given

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getCharState(userDetails));
            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.",
                    exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("상대방 캐릭터 조회")
    class getPartnerStateTest {

        @Test
        @DisplayName("상대방 캐릭터 조회 성공")
        void getPartnerState() {

            //given
            Long userId = userDetails.getMember().getId();

            given(matchingRepository.getMatching(userId))
                    .willReturn(Optional.ofNullable(matching));
            Long partnerId = userId.equals(matching.getRequesterId())
                    ? matching.getRespondentId()
                    : matching.getRequesterId();
            given(memberRepository.findById(partnerId))
                    .willReturn(Optional.ofNullable(existMember2));

            //when
            CharResponseDto result = characterService.getPartnerState(userDetails);

            //then
            Assertions.assertEquals(existMember2.getNickname(),
                    result.getNickname());
            Assertions.assertEquals(existMember2.getCharacter().getLevel(),
                    result.getLevel());
            Assertions.assertEquals(existMember2.getCharacter().getMoney(),
                    result.getMoney());
        }

        @Test
        @DisplayName("상대방 캐릭터 조회 실패 - 매칭이 안 되어 있음")
        void getPartnerStateFail1() {
            //given

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getPartnerState(userDetails));

            //then
            Assertions.assertEquals("매칭이 되어있지 않습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("상대방 캐릭터 조회 실패 - 파트너 유저 정보 없음")
        void getPartnerStateFail2() {
            //given
            Long userId = userDetails.getMember().getId();

            given(matchingRepository.getMatching(userId))
                    .willReturn(Optional.ofNullable(matching));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getPartnerState(userDetails));

            //then
            Assertions.assertEquals("파트너 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("푸터용 캐릭터 조회")
    class getCharacterInFooterTest {

        @Test
        @DisplayName("푸터용 캐릭터 조회 성공")
        void getCharacterInFooter() {
            //given
            Long myId = userDetails.getMember().getId();
            given(memberRepository.findById(myId))
                    .willReturn(Optional.ofNullable(existMember));
            given(matchingRepository.getMatching(myId))
                    .willReturn(Optional.ofNullable(matching));

            Long partnerId = myId.equals(matching.getRequesterId())
                    ? matching.getRespondentId()
                    : matching.getRequesterId();
            given(memberRepository.findById(partnerId))
                    .willReturn(Optional.ofNullable(existMember2));

            //when
            FooterResponseDto result = characterService.getCharacterInFooter(userDetails);

            //then
            Assertions.assertEquals(userDetails.getMember().getNickname()
                    , result.getMyNickname());
            Assertions.assertEquals(userDetails.getMember().getId()
                    , result.getMyId());
            Assertions.assertEquals(existMember2.getId()
                    , result.getPartnerId());
            Assertions.assertEquals(existMember2.getNickname()
                    , result.getPartnerNickname());
        }

        @Test
        @DisplayName("푸터용 캐릭터 조회 실패 - 로그인한 유저 정보 없는 경우")
        void getCharacterInFooterFail1() {
            //given

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getCharacterInFooter(userDetails));

            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다."
                    , exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("푸터용 캐릭터 조회 실패 - 매칭이 안 되어 있는 경우")
        void getCharacterInFooterFail2() {
            //given
            Long myId = userDetails.getMember().getId();
            given(memberRepository.findById(myId))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getCharacterInFooter(userDetails));

            //then
            Assertions.assertEquals("매칭이 되어있지 않습니다."
                    , exception.getErrorCode().getMessage());
        }
        @Test
        @DisplayName("푸터용 캐릭터 조회 실패 - 파트너 유저 정보가 없는 경우")
        void getCharacterInFooterFail3() {
            //given
            Long myId = userDetails.getMember().getId();
            given(memberRepository.findById(myId))
                    .willReturn(Optional.ofNullable(existMember));
            given(matchingRepository.getMatching(myId))
                    .willReturn(Optional.ofNullable(matching));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> characterService.getCharacterInFooter(userDetails));

            //then
            Assertions.assertEquals("파트너 유저 정보를 찾을 수 없습니다."
                    , exception.getErrorCode().getMessage());
        }
    }
}