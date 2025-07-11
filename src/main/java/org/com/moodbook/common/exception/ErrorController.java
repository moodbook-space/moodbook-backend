package org.com.moodbook.common.exception;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 컨트롤러의 예외를 한 곳에서 처리하기 위한 어노테이션
@Slf4j
public class ErrorController {

    /**
     * 직접 정의한 비즈니스 예외(BaseException)를 처리
     * @param e BaseException 또는 그 하위 타입의 예외 객체
     * @return ErrorCode에 정의된 HTTP 상태 코드와 에러 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(BaseException.class) // ②
    protected ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        // BaseException에서 미리 정의된 HttpStatus와 ErrorCode를 사용하여
        // 클라이언트에게 보낼 응답(ApiErrorResponse)을 생성하고 ResponseEntity로 감싸 반환.
        return ResponseEntity
            .status(e.getHttpStatus())                      // 예외 객체(e)에 저장된 HTTP 상태 코드를 가져옵니다. (예: 404 NOT_FOUND)
            .body(ErrorResponse.from(e.getErrorCode()));    // 예외 객체(e)의 ErrorCode로 에러 응답 DTO를 생성합니다.
    }

    /**
     * @Valid 어노테이션을 사용한 유효성 검증(Validation)에 실패했을 때 발생하는 예외를 처리
     * 주로 DTO의 필드 값이 @NotBlank, @NotNull 등의 조건을 만족하지 못했을 때 발생
     * @param e MethodArgumentNotValidException 예외 객체
     * @return 400 Bad Request 상태 코드와 함께, 어떤 필드가 왜 잘못되었는지에 대한 상세 메시지를 담은 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 이 핸들러는 항상 400 (Bad Request) 상태 코드를 반환합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 유효성 검사에 실패한 필드들의 에러 메시지를 리스트에 담기
        List<String> params = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            // "필드명: 기본 에러 메시지" 형식의 문자열을 리스트에 추가
            // 예: "username: must not be blank"
            params.add(error.getField() + ": " + error.getDefaultMessage());
        }
        // 리스트에 담긴 모든 에러 메시지를 ", "로 연결하여 하나의 문자열로 만들기
        String errorMessage = String.join(", ", params);

        // VALIDATION_FAILED 라는 기본 ErrorCode로 응답 객체를 생성합니다.
        ErrorResponse response = ErrorResponse.from(ErrorCode.VALIDATION_FAILED);
        // 생성된 상세 에러 메시지(errorMessage)로 기본 메시지를 덮어씀
        response.changeMessage(errorMessage);

        return response; // 최종 에러 응답을 반환
    }

    /**
     * 위에서 처리하지 못한 모든 종류의 런타임 예외(RuntimeException)를 처리하는 최후의 보루
     * 예상치 못한 서버 오류가 발생했을 때 호출
     * @param e RuntimeException 예외 객체
     * @return 500 Internal Server Error 상태 코드와 고정된 에러 메시지를 담은 응답
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 이 핸들러는 항상 500 (Internal Server Error) 상태 코드를 반환합니다.
    @ExceptionHandler(RuntimeException.class)
    protected ErrorResponse handleRuntimeException(RuntimeException e) {
        // 서버 로그에 실제 에러 원인을 기록하여 개발자가 디버깅할 수 있도록 함
        // 클라이언트에게는 상세 원인을 노출하지 않음
        log.error(e.getMessage());
        // 미리 정의된 INTERNAL_SERVER_ERROR 응답을 클라이언트에게 반환합니다.
        return ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
