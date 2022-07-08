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
    MATCHED_PARTNER(HttpStatus.BAD_REQUEST, "상대방이 이미 매칭 중입니다."),

    NOT_ENOUGH_MONEY(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    ALREADY_GOT_ITEM(HttpStatus.BAD_REQUEST, "이미 구매하신 물품입니다."),

    /* 404 NOT_FOUND : Resource 찾을 수 없음 */
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_REQUESTER(HttpStatus.NOT_FOUND, "요청을 보낸 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_SEARCHED_MEMBER(HttpStatus.NOT_FOUND,"검색한 유저를 찾을 수 없습니다."),
    NOT_FOUND_PARTNER(HttpStatus.NOT_FOUND, "파트너 유저 정보를 찾을 수 없습니다."),
    NOT_FOUND_MATCHING(HttpStatus.NOT_FOUND,"매칭이 되어있지 않습니다.");



    private final HttpStatus httpStatus;
    private final String message;

}
