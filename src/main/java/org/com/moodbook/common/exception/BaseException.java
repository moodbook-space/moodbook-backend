package org.com.moodbook.common.exception;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException {

    public static final BaseException VALIDATION_FAILED = new BaseException(ErrorCode.VALIDATION_FAILED);
    public static final BaseException INVALID_INPUT_VALUE = new BaseException(ErrorCode.INVALID_INPUT_VALUE);
    public static final BaseException INVALID_TOKEN = new BaseException(ErrorCode.INVALID_TOKEN);
    public static final BaseException ALREADY_EXIST_EMAIL = new BaseException(ErrorCode.ALREADY_EXIST_EMAIL);
    public static final BaseException MEMBER_NOT_FOUND = new  BaseException(ErrorCode.MEMBER_NOT_FOUND);
    public static final BaseException AUTHENTICATION_FAILED = new BaseException(ErrorCode.AUTHENTICATION_FAILED);
    public static final BaseException EXPIRED_ACCESS_TOKEN = new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
    public static final BaseException ACCESS_DENIED = new BaseException(ErrorCode.ACCESS_DENIED);
    public static final BaseException EMAIL_NOT_VERIFIED =  new BaseException(ErrorCode.EMAIL_NOT_VERIFIED);
    public static final BaseException FORBIDDEN = new BaseException(ErrorCode.FORBIDDEN);
    public static final BaseException EXPIRED_OR_PREVIOUS_REFRESH_TOKEN = new BaseException(ErrorCode.EXPIRED_OR_PREVIOUS_REFRESH_TOKEN);
    public static final BaseException INVALID_PASSWORD = new  BaseException(ErrorCode.INVALID_PASSWORD);
    public static final BaseException MEMBER_DEACTIVATED =  new BaseException(ErrorCode.MEMBER_DEACTIVATED);
    public static final BaseException ALREADY_EXIST_CONTACT =  new BaseException(ErrorCode.ALREADY_EXIST_CONTACT);
    public static final BaseException EMOTION_NOT_FOUND = new BaseException(ErrorCode.EMOTION_NOT_FOUND);
    public static final BaseException CHATROOM_NOT_FOUND = new BaseException(ErrorCode.CHATROOM_NOT_FOUND);
    public static final BaseException INTERNAL_SERVER_ERROR = new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final BaseException AWSS3_UPLOAD_ERROR = new BaseException(ErrorCode.AWSS3_UPLOAD_ERROR);
    public static final BaseException AWSS3_GET_ERROR = new BaseException(ErrorCode.AWSS3_GET_ERROR);
    public static final BaseException BOOK_NOT_FOUND = new BaseException(ErrorCode.BOOK_NOT_FOUND);
    public static final BaseException ALREADY_IN_BOOKMARK = new BaseException(ErrorCode.ALREADY_IN_BOOKMARK);
    public static final BaseException BOOKMARK_NOT_FOUND = new BaseException(ErrorCode.BOOKMARK_NOT_FOUND);

    public static final BaseException REVIEW_FORBIDDEN = new BaseException(ErrorCode.REVIEW_FORBIDDEN);
    public static final BaseException REVIEW_NOT_FOUND = new BaseException(ErrorCode.REVIEW_NOT_FOUND);

    public static final BaseException LEADER_NOT_FOUND = new BaseException(ErrorCode.LEADER_NOT_FOUND);
    public static final BaseException NOTIFICATION_NOT_FOUND = new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND);

    private final ErrorCode errorCode;

    // 의도적인 예외이므로 stack trace 제거 (불필요한 예외처리 비용 제거)
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

}
