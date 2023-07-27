package ru.emelkrist.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AnswerConsumer {

    /**
     * Метод для получения ответа от микросервиса node
     * из прослушиваемой очереди для сообщений с ответом
     * и вывода этого ответа в чат.
     * @param sendMessage полученное из очереди сообщение для отправки
     */
    void consume(SendMessage sendMessage);
}
