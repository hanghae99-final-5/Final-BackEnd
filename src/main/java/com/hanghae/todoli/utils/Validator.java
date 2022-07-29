package com.hanghae.todoli.utils;

import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.member.dto.SignupRequestDto;
import com.hanghae.todoli.todo.dto.TodoRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Validator {

    private final MemberRepository memberRepository;

    //아이디 유효성 확인
    public void validSignup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();
        String password = signupRequestDto.getPassword();

        String regex = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-z]+$";
        if (!Pattern.matches(regex, username)) {
            throw new CustomException(ErrorCode.WRONG_PATTERN_EMAIL);
        }
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID);
        }
        if (nickname.equals("") || password.equals("")) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }
    }

    //투두 유효성 체크
    public void validTodo(TodoRegisterDto registerDto) {
        LocalDate now = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (registerDto.getStartDate() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_START_DATE);
        }
        if (registerDto.getEndDate() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_END_DATE);
        }
        if (registerDto.getContent() == null) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }
        if (registerDto.getContent().equals("")) {
            throw new CustomException(ErrorCode.NO_INPUT_CONTENT);
        }
        if (registerDto.getDifficulty() == 0) {
            throw new CustomException(ErrorCode.NO_INPUT_DIFFICULTY);
        }
        if (registerDto.getTodoType() == 0) {
            throw new CustomException(ErrorCode.NO_INPUT_TODO_TYPE);
        }
        if (registerDto.getStartDate().isBefore(now)) {
            throw new CustomException(ErrorCode.BAD_REQUEST_START_DATE);
        }
        if (registerDto.getEndDate().isBefore(registerDto.getStartDate())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_END_DATE);
        }
    }
}
