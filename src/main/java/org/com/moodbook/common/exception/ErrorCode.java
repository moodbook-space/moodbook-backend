package org.com.moodbook.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
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
  ALREADY_EXIST_CONTACT(HttpStatus.BAD_REQUEST,"ALREADY_EXIST_CONTACT","이미 등록된 연락처입니다"),
  ALREADY_EXIST_JOIN(HttpStatus.BAD_REQUEST, "ALREADY_EXIST_JOIN", "이미 신청한 모임입니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
  INVALID_ACTION(HttpStatus.BAD_REQUEST, "INVALID_ACTION", "올바른 작업이 아닙니다."),
  ALREADY_IN_BOOKMARK(HttpStatus.BAD_REQUEST, "ALREADY_IN_BOOKMARK", "이미 북마크에 존재하는 도서입니다"),
  ALREADY_VERIFIED(HttpStatus.BAD_REQUEST,"ALREADY_VERIFIED","이미 인증된 메일입니다"),

  /* 401 - Unauthorized */
  AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "AUTHENTICATION_FAILED", "인증에 실패했습니다."),
  EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_ACCESS_TOKEN", "Access Token이 만료되었습니다. 토큰을 재발급해주세요"),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),
  EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED,"EMAIL_NOT_VERIFIED","이메일 인증 후 진행해주세요"),
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED_ACCESS","권한이 없습니다"),
  MEMBER_ALREADY_DEACTIVATED(HttpStatus.BAD_REQUEST,"MEMBER_ALREADY_DEACTIVATED","이미 비활성화 된 회원입니다"),
  /* 403 - Forbidden */
  FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근할 수 있는 권한이 없습니다."),
  EXPIRED_OR_PREVIOUS_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "EXPIRED_OR_PREVIOUS_REFRESH_TOKEN",
      "만료되었거나 이전에 발급된 Refresh Token입니다."),
  MEMBER_DEACTIVATED(HttpStatus.UNAUTHORIZED,"MEMBER_DEACTIVATED","비활성화된 멤버입니다"),
  CHAT_ROOM_MEMBER_NOT_LEADER(HttpStatus.UNAUTHORIZED,"CHAT_ROOM_MEMBER_NOT_LEADER","방장 권한이 없습니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"INVALID_REFRESH_TOKEN","유효하지 않는 토큰입니다"),
  REVIEW_FORBIDDEN(HttpStatus.UNAUTHORIZED, "REVIEW_FORBIDDEN", "본인의 리뷰만 수정/삭제할 수 있습니다."),
  REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED,"REFRESH_TOKEN_MISMATCH","리프레쉬 토큰이 불일치 합니다"),

  /* 404 - Not Found */
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "해당 책을 찾을 수 없습니다."),
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", "해당 독후감을 찾을 수 없습니다."),
  MEETING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND", "해당 모임을 찾을 수 없습니다."),
  EMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "EMOTION_NOT_FOUND", "해당 감정을 찾을 수 없습니다."),
  BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK_NOT_FOUND", "북마크에 해당 책이 존재하지 않습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"REFRESH_TOKEN_NOT_FOUND","리프레쉬 토큰은 없습니다"),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_NOT_FOUND","해당 사용자를 찾을 수 없습니다"),
  CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM_REQUEST_NOT_FOUND", "해당 채팅방을 찾을 수 없습니다."),
  INVALID_PASSWORD(HttpStatus.NOT_FOUND,"INVALID_PASSWORD","비밀번호가 일치하지 않습니다"),
  JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "JOIN_REQUEST_NOT_FOUND", "신청 요청을 찾을 수 없습니다."),
  COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
  POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST_NOT_FOUND", "게시글을 찾을 수 없습니다."),
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),
  LEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "LEADER_NOT_FOUND", "채팅방 리더를 찾을 수 없습니다."),
  AWSS3_NO_FILE(HttpStatus.BAD_REQUEST, "AWSS3_NO_FILE", "S3에 해당 파일이 없습니다."),
  ACCESS_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"ACCESS_TOKEN_NOT_FOUND","토큰이 존재하지 않습니다"),
  /* 500 - Internal Server Error */
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에 오류가 발생했습니다."),
  AWSS3_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWSS3_UNKNOWN_ERROR", "S3 연결 과정에서 에러가 발생했습니다"),
  AWSS3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWSS3_UPLOAD_ERROR", "S3 사진 업로드에 실패했습니다"),
  AWSS3_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWSS3_DELETE_ERROR", "S3에서 사진 삭제에 실패했습니다."),
  EMOTION_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"EMOTION_SAVE_ERROR","감정 분석,저장에 실패했습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

}
