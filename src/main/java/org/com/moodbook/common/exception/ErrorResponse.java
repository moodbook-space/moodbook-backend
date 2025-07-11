package org.com.moodbook.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private String code;            // 상태코드
    private String message;         // 커스텀 에러 메세지

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
    }

    public void changeMessage(String message) {
        this.message = message;
    }

}
