package com.hanghae.todoli.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    PASSWORD_NOT_SAME(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_ID(HttpStatus.BAD_REQUEST,"이미 중복된 아이디입니다."),
    WRONG_PATTERN_EMAIL(HttpStatus.BAD_REQUEST, "이메일 형식이 아닙니다."),
    NOT_MATCHING_PARTNER(HttpStatus.BAD_REQUEST,"매칭되어있는 상대가 아닙니다."),
    MATCHED_PARTNER(HttpStatus.BAD_REQUEST, "상대방이 이미 매칭 중입니다."),
    MATCHED_MEMBER(HttpStatus.BAD_REQUEST, "자신이 이미 매칭 중입니다."),
    NOT_MATCHED_MEMBER(HttpStatus.BAD_REQUEST, "자신이 매칭되어있지 않습니다."),
    NOT_ENOUGH_MONEY(HttpStatus.BAD_REQUEST, "금액이 부족합니다."),
    ALREADY_GOT_ITEM(HttpStatus.BAD_REQUEST, "이미 구매하신 물품입니다."),
    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST,"아이템을 먼저 구매해 주세요."),
    CONFIRMED_TODO(HttpStatus.BAD_REQUEST, "이미 인증된 Todo입니다."),
    NOT_CONFIRMED_TODO(HttpStatus.BAD_REQUEST, "인증된 Todo가 아닙니다."),

    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다"),
    PERMISSION_DENIED(HttpStatus.BAD_REQUEST,"토큰이 유효하지 않습니다."),

    /* validator 400 BAD_REQUEST, 403 FORBIDDEN */
    NO_INPUT_START_DATE(HttpStatus.BAD_REQUEST, "시작날짜를 선택해주세요"),
    NO_INPUT_END_DATE(HttpStatus.BAD_REQUEST, "종료날짜를 선택해주세요"),
    NO_INPUT_CONTENT(HttpStatus.BAD_REQUEST, "Todo 내용을 입력해주세요"),
    NO_INPUT_DIFFICULTY(HttpStatus.BAD_REQUEST, "난이도를 선택해주세요"),
    NO_INPUT_TODO_TYPE(HttpStatus.BAD_REQUEST, "Todo 타입을 선택해주세요"),
    FORBIDDEN_START_DATE(HttpStatus.FORBIDDEN,"시작날짜를 현재날짜 이후로 설정해주세요."),
    FORBIDDEN_END_DATE(HttpStatus.FORBIDDEN,"종료날짜를 시작날짜 이후로 설정해주세요."),
    /* 403 FORBIDDEN : 잘못된 접근 */
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),

    NOT_TODO_WRITER(HttpStatus.FORBIDDEN,"Todo 작성자가 아닙니다."),

    /* 404 NOT_FOUND : Resource 찾을 수 없음 */
    NO_ITEM(HttpStatus.NOT_FOUND, "아이템이 존재하지 않습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_REQUESTER(HttpStatus.NOT_FOUND, "요청을 보낸 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_SEARCHED_MEMBER(HttpStatus.NOT_FOUND,"상대방 유저를 찾을 수 없습니다."),
    NOT_FOUND_PARTNER(HttpStatus.NOT_FOUND, "파트너 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_TODO(HttpStatus.NOT_FOUND,"Todo가 존재하지 않습니다."),
    NOT_FOUND_MATCHING(HttpStatus.NOT_FOUND,"매칭이 되어있지 않습니다.");


    private final HttpStatus httpStatus;
    private final String message;

}
