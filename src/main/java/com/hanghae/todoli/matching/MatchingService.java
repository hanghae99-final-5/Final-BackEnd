package com.hanghae.todoli.matching;

import com.hanghae.todoli.alarm.Alarm;
import com.hanghae.todoli.alarm.AlarmRepository;
import com.hanghae.todoli.alarm.AlarmType;
import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.CharacterService;
import com.hanghae.todoli.character.ThumbnailDto;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import com.hanghae.todoli.todo.TodoRepository;
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
            throw new IllegalArgumentException("이메일 형식이 아닙니다.");
        }
        Long myId = userDetails.getMember().getId();
        Member myInfo = memberRepository.findById(myId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member target = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("검색한 유저가 존재하지 않습니다.")
        );
        Matching matching =matchingRepository.getMatching(target.getId()).orElse(null);
        String searchedUserPartnerName = "";
        if (matching != null) {
            Long searchedUserPartnerId = target.getId()
                    .equals(matching.getRequesterId()) ? matching.getRespondentId() : matching.getRequesterId();

            Member partner = memberRepository.findById(searchedUserPartnerId).orElseThrow(
                    () -> new IllegalArgumentException("상대방의 매칭 유저가 존재하지 않습니다.")
            );
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
                () -> new IllegalArgumentException("상대방이 존재하지 않습니다.")
        );
        if (targetMember.getMatchingState()) {
            throw new IllegalArgumentException("상대방이 이미 매칭 중입니다.");
        }

        //현재 날짜 출력
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Alarm alarm = Alarm.builder()
                .alarmDate(now)
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
                () -> new IllegalArgumentException("로그인한 상태가 없습니다.")
        );
//        Member member = userDetails.getMember();

        //상대방 찾기
        Member targetMember = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("상대방이 존재하지 않습니다.")
        );
        Matching matching = matchingRepository.getMatching(member.getId()).orElseThrow(
                () -> new IllegalArgumentException("매칭상태가 없습니다.")
        );
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
                () -> new IllegalArgumentException("로그인한 상태가 없습니다.")
        );
//        Member member = userDetails.getMember();
        Member sender = memberRepository.findById(senderId).orElseThrow(
                () -> new IllegalArgumentException("상대방이 없습니다.")
        );

        //멤버의 매칭 상태 변경
        member.changeMatchingState(member);
        sender.changeMatchingState(sender);

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
