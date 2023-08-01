package ru.emelkrist.service;

import ru.emelkrist.dto.MailParams;

public interface MailSenderService {
    /**
     * Метод для отправки электронного сообщения для регистрации/активации аккаунта в системе
     * @param mailParams данные для отправки письма и активации
     */
    void send(MailParams mailParams);
}
