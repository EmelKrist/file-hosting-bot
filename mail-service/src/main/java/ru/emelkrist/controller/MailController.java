package ru.emelkrist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.emelkrist.dto.MailParams;
import ru.emelkrist.service.MailSenderService;

@RestController
@RequestMapping("/mail")
public class MailController {
    private final MailSenderService mailSenderService;

    public MailController(MailSenderService mailSenderService) {
        this.mailSenderService = mailSenderService;
    }

    /**
     * Метод для обработки POST запроса на отправку сообщения для активации
     * Note: Получает из тела запроса json с почтовыми параметрами пользователя (id и email)
     * @param mailParams почтовые параметры
     * @return ответ на запрос
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams){
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
