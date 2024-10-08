package dev.backend.wakuwaku.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionStatus {
    // common exception
    INVALID_PARAMETER(BAD_REQUEST, 2000, "잘못된 요청이 존재합니다."),
    INVALID_URL(BAD_REQUEST, 2001, "잘못된 URL 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2002,"서버 내부 오류입니다."),
    NOT_EXISTED_FILE(NOT_EXTENDED, 2003,"존재하지 않는 파일입니다."),

    // member exception
    DUPLICATED_EMAIL(CONFLICT, 3000, "중복된 이메일이 존재합니다."),
    NONE_USER(NOT_FOUND, 3001, "존재하지 않는 회원입니다."),
    DEACTIVATED_USER(NOT_FOUND ,3002, "이미 탈퇴한 사용자입니다."),


    // google places exception
    INVALID_SEARCH_WORD(BAD_REQUEST, 4000, "검색어를 입력하지 않았습니다."),
    NONE_PHOTO_URL(NOT_FOUND, 4001, "PHOTO URL이 존재하지 않습니다."),
    NOT_EXISTED_PLACE_ID(BAD_REQUEST, 4002, "PLACE ID 가 존재하지 않습니다."),
    NOT_EXISTED_DETAILS_RESPONSE(BAD_REQUEST, 4003, "Details Response 가 존재하지 않습니다."),



    // oauth exception
    FALIED_TO_LOGIN(NOT_FOUND ,5000, "로그인 중 오류가 발생했습니다. 다시 시도해 주세요."),
    NOT_EXISTED_SOCIAL_TYPE(NOT_FOUND ,5001, "지원하지 않는 소셜 타입입니다."),
    NOT_EXISTED_MEMBER_INFO(NOT_FOUND ,5002, "사용자 정보를 찾을 수 없습니다."),


    // like exception
    INVALID_LIKE_REQUEST(BAD_REQUEST ,6000, "찜이 불가능한 식당입니다."),
    FALIED_TO_LIKE_BECAUSE_MEMBER(BAD_REQUEST ,6001, "찜 요청이 불가능한 사용자입니다."),
    LIKE_NOT_FOUND_EXCEPTION(NOT_FOUND ,6002, "찜 기록이 존재하지 않습니다."),
    NOT_FOUND_RESTAURANT_INFO(NOT_FOUND ,6003, "식당 정보를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER_INFO(NOT_FOUND ,6004, "사용자 정보를 찾을 수 없습니다."),
    ALREADY_LIKED_EXCEPTION(BAD_REQUEST ,6005, "이미 찜한 식당입니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
