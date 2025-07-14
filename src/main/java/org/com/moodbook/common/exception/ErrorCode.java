package org.com.moodbook.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor

public enum ErrorCode {

  /* 400 - Bad Request */
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "입력 값이 올바르지 않습니다."),
  VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "입력값 유효성 검사에 실패했습니다."),
  INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
  ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "ALREADY_EXIST_EMAIL", "이미 가입된 이메일입니다."),
  INVALID_MEETING_TYPE(HttpStatus.BAD_REQUEST, "INVALID_MEETING_TYPE", "올바른 모임 유형이 아닙니다."),
  TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "TAG_NOT_FOUND", "유효하지 않은 태그입니다."),
  ALREADY_EXIST_JOIN(HttpStatus.BAD_REQUEST, "ALREADY_EXIST_JOIN", "이미 신청한 모임입니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  INVALID_ACTION(HttpStatus.BAD_REQUEST, "INVALID_ACTION", "올바른 작업이 아닙니다."),

  /* 401 - Unauthorized */
  AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "AUTHENTICATION_FAILED", "인증에 실패했습니다."),
  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_ACCESS_TOKEN", "Access Token이 만료되었습니다. 토큰을 재발급해주세요"),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),

  /* 403 - Forbidden */
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근할 수 있는 권한이 없습니다."),
  EXPIRED_OR_PREVIOUS_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "EXPIRED_OR_PREVIOUS_REFRESH_TOKEN",
      "만료되었거나 이전에 발급된 Refresh Token입니다."),

  /* 404 - Not Found */
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다."),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "해당 책을 찾을 수 없습니다."),
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "해당 독후감을 찾을 수 없습니다."),
  MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND", "해당 모임을 찾을 수 없습니다."),
  EMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "EMOTION_NOT_FOUND", "해당 감정을 찾을 수 없습니다."),
  JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "JOIN_REQUEST_NOT_FOUND", "신청 요청을 찾을 수 없습니다."),

  /* 500 - Internal Server Error */
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에 오류가 발생했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
