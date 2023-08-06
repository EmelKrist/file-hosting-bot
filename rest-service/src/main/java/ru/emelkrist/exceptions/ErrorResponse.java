package ru.emelkrist.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
/**
 * Класс ответа с ошибкой
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int  code;
    private String name;
    private String path;
    private String message;
}
