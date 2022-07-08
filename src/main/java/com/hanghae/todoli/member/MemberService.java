package com.hanghae.todoli.member;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;

    //회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();

        idCheck(username);

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        //멤버생성과 동시에 캐릭터, 장착아이템 같이 생성
        EquipItem equipItem = new EquipItem();
        equipItemRepository.save(equipItem);
        Character character = new Character(equipItem);
        characterRepository.save(character);
        Member Member = new Member(username, nickname, password, false, character);
        memberRepository.save(Member);

    }


    @Transactional
    public Member login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Member Member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), Member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);
        }
        return Member;
    }

    //아이디 중복확인
    private void idCheck(String username) {
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID);
        }
    }
}
