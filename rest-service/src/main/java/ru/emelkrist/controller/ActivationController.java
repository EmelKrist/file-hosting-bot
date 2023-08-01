package ru.emelkrist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.emelkrist.service.UserActivationService;

@RestController
@RequestMapping("/user")
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    /**
     * Метод для обработки GET запроса для активации пользователя
     * Note: получает зашифрованный идентифаикатор пользоватеоля из параметров запроса
     * и активирует данного пользователя
     * @param id зашифрованный идентификатор пользователя
     * @return сущность с ответом
     */
    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Активация успешно завершена!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
