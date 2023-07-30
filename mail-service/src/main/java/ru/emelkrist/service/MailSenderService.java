package ru.emelkrist.service;

import ru.emelkrist.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
