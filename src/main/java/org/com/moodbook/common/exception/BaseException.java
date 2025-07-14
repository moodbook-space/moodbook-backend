package org.com.moodbook.common.exception;

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

    public static final BaseException AUTHENTICATION_FAILED = new BaseException(ErrorCode.AUTHENTICATION_FAILED);
    public static final BaseException EXPIRED_ACCESS_TOKEN = new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
    public static final BaseException ACCESS_DENIED = new BaseException(ErrorCode.ACCESS_DENIED);

    public static final BaseException FORBIDDEN = new BaseException(ErrorCode.FORBIDDEN);
    public static final BaseException EXPIRED_OR_PREVIOUS_REFRESH_TOKEN = new BaseException(ErrorCode.EXPIRED_OR_PREVIOUS_REFRESH_TOKEN);

    public static final BaseException USER_NOT_FOUND = new BaseException(ErrorCode.USER_NOT_FOUND);
    public static final BaseException EMOTION_NOT_FOUND = new BaseException(ErrorCode.EMOTION_NOT_FOUND);

    public static final BaseException INTERNAL_SERVER_ERROR = new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
    public static final BaseException AWSS3_UPLOAD_ERROR = new BaseException(ErrorCode.AWSS3_UPLOAD_ERROR);
    public static final BaseException AWSS3_GET_ERROR = new BaseException(ErrorCode.AWSS3_GET_ERROR);

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
