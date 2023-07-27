package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    /**
     * Метод для предоставления ответа от node
     * в очередь ANSWER_MESSAGE
     * @param sendMessage отправляемое сообщение(ответ)
     */
    void producerAnswer(SendMessage sendMessage);
}
