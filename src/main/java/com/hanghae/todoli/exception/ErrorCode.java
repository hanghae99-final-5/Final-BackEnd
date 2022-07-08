package com.hanghae.todoli.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    PASSWORD_NOT_SAME(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_ID(HttpStatus.BAD_REQUEST,"이미 중복된 아이디입니다."),
    NOT_MATCHING_PARTNER(HttpStatus.BAD_REQUEST,"매칭되어있는 상대가 아닙니다."),

    /* 404 NOT_FOUND : Resource 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    PARTNER_NOT_FOUND(HttpStatus.NOT_FOUND, "파트너 유저 정보를 찾을 수 없습니다."),

    MATCHING_NOT_FOUND(HttpStatus.NOT_FOUND,"매칭이 되어있지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
