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

    @PostMapping("/send")
    public ResponseEntity<?> sendActivationMail(@RequestBody MailParams mailParams){
        mailSenderService.send(mailParams);
        return ResponseEntity.ok().build();
    }
}
