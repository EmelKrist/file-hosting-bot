package ru.emelkrist.dispatcher.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    /**
     * Метод для предостовления(отправки) обновления в чате
     * в очередь для обработки ответа в node.
     * @param rabbitQueue очередь для отправки сообщения
     * @param update обновление в чате
     */
    void produce(String rabbitQueue, Update update);
}
