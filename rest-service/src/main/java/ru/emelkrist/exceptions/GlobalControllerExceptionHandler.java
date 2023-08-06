package ru.emelkrist.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    /**
     * "Ловец" исключений со статусом 400
     *
     * @param request информация о запросе
     * @param exception выброшенное исключение
     * @return информация об ошибке
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileNotFoundException.class)
    private ResponseEntity<String> handleException(HttpServletRequest request, Exception exception) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad request",
                request.getRequestURL().toString(),
                exception.getMessage()
        );
        return new ResponseEntity<>(response.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
