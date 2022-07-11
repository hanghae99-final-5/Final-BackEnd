package com.hanghae.todoli.matching;

import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
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
import com.hanghae.todoli.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.hanghae.todoli.alarm.AlarmType.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final AlarmRepository alarmRepository;
    private final MatchingRepository matchingRepository;
    private final TodoRepository todoRepository;

    //상대방 찾기
    @Transactional
    public MatchingResponseDto searchMember(String username, UserDetailsImpl userDetails) {
        String regex ="^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$";
        if (!Pattern.matches(regex,username)) {
            throw new CustomException(ErrorCode.WRONG_PATTERN_EMAIL);
        }
        Long myId = userDetails.getMember().getId();
        Member myInfo = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Member target = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEARCHED_MEMBER));

        Matching matching =matchingRepository.getMatching(target.getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MATCHING));

        String searchedUserPartnerName = "";
        if (matching != null) {
            Long searchedUserPartnerId = target.getId()
                    .equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

            Member partner = memberRepository.findById(searchedUserPartnerId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER));

            searchedUserPartnerName = partner.getUsername();
        }
        List<ThumbnailDto> targetThumbnailDtos = getThumbnailDtos(target);

        return MatchingResponseDto.builder()
                .myMatchingState(myInfo.getMatchingState())
                .memberId(target.getId())
                .nickname(target.getNickname())
                .partnerMatchingState(target.getMatchingState())
                .searchedUserPartner(searchedUserPartnerName)
                .thumbnailCharImg(new CharacterImg().getThumbnailCharImg())
                .equipItems(targetThumbnailDtos)
                .build();
    }

    //상대방 초대
    @Transactional
    public void inviteMatching(Long memberId, UserDetailsImpl userDetails) {
        //상대방 찾기
        Member targetMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_SEARCHED_MEMBER)
        );
        if (targetMember.getMatchingState()) {
            throw new CustomException(ErrorCode.MATCHED_PARTNER);
        }

        //현재 날짜 출력
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Alarm alarm = Alarm.builder()
                .alarmDate(now)
                .alarmState(0L)
                .alarmType(ACCEPTANCE)
                .member(targetMember)
                .senderId(userDetails.getMember().getId())
                .message(userDetails.getMember().getNickname() + "님과 함께하시겠습니까?")
                .build();

        alarmRepository.save(alarm);
    }

    //매칭 취소
    @Transactional
    public void cancelMatching(Long memberId, UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );
//        Member member = userDetails.getMember();

        //상대방 찾기
        Member targetMember = memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_PARTNER)
        );
        Matching matching = matchingRepository.getMatching(member.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MATCHING));

        //매칭 정보 삭제
        matchingRepository.delete(matching);
        //멤버의 매칭 상태 변경
        member.changeMatchingState(member);
        targetMember.changeMatchingState(targetMember);
        //멤버의 투두리스트 삭제
        todoRepository.deleteAllByWriterIdAndTodoType(member.getId(),1);
        todoRepository.deleteAllByWriterIdAndTodoType(targetMember.getId(),1);
    }

    //매칭 수락
    @Transactional
    public void acceptMatching(Long senderId, UserDetailsImpl userDetails) {
        Long id = userDetails.getMember().getId();
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        Member sender = memberRepository.findById(senderId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_REQUESTER)
        );

        //멤버의 매칭 상태 변경
        member.changeMatchingState(member);
        sender.changeMatchingState(sender);


        List<Alarm> allByAlarm = alarmRepository.findAllByAlarm(member.getId());

        for(Alarm a : allByAlarm){
            a.setAlarmState(1L);
        }

        //매칭에 매칭 정보 저장
        Matching matching = new Matching(senderId, member.getId());
        matchingRepository.save(matching);
    }

    //아이템 리스트dto에 추가
    private List<ThumbnailDto> getThumbnailDtos(Member Info) {
        List<ThumbnailDto> myEquipItemList = new ArrayList<>();
        EquipItem myEquipItem = Info.getCharacter().getEquipItem();
        Long hairId = myEquipItem.getHairId();
        Long clothId = myEquipItem.getClothId();
        Long accessoryId = myEquipItem.getAccessoryId();
        if (hairId != null) {
            Item hair = itemRepository.findById(hairId).orElse(null);
            ThumbnailDto thumbnailDto1 = new ThumbnailDto(hair.getId(),hair.getThumbnailImg(),hair.getCategory());
            myEquipItemList.add(thumbnailDto1);
        }
        if (clothId != null) {
            Item cloth = itemRepository.findById(clothId).orElse(null);
            ThumbnailDto thumbnailDto2 = new ThumbnailDto(cloth.getId(),cloth.getThumbnailImg(),cloth.getCategory());
            myEquipItemList.add(thumbnailDto2);
        }
        if (accessoryId != null) {
            Item accessory = itemRepository.findById(accessoryId).orElse(null);
            ThumbnailDto thumbnailDto3 = new ThumbnailDto(accessory.getId(),accessory.getThumbnailImg(),accessory.getCategory());
            myEquipItemList.add(thumbnailDto3);
        }
        return myEquipItemList;
    }

}
