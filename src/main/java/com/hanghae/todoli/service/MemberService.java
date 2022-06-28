package com.hanghae.todoli.service;

import com.hanghae.todoli.dto.LoginRequestDto;
import com.hanghae.todoli.dto.SignupRequestDto;
import com.hanghae.todoli.models.Character;
import com.hanghae.todoli.models.EquipItem;
import com.hanghae.todoli.models.Member;
import com.hanghae.todoli.repository.CharacterRepository;
import com.hanghae.todoli.repository.EquipItemRepository;
import com.hanghae.todoli.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;

    //회원가입
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


    public Member login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Member Member = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), Member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return Member;
    }

    //아이디 중복확인
    private void idCheck(String username) {
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("이미 중복된 아이디입니다.");
        }
    }
}
