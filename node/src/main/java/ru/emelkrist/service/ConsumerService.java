package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    /**
     * Метод для получения обновления в формате текста
     * от микросервиса dispatcher из прослушиваемой очереди
     * TEXT_MESSAGE_UPDATE
     * @param update обновление, полученное из чата
     */
    void consumeTextMessageUpdates(Update update);
    /**
     * Метод для получения обновления в формате документа
     * от микросервиса dispatcher из прослушиваемой очереди
     * DOC_MESSAGE_UPDATE
     * @param update обновление, полученное из чата
     */
    void consumeDocMessageUpdates(Update update);
    /**
     * Метод для получения обновления в формате фото
     * от микросервиса dispatcher из прослушиваемой очереди
     * PHOTO_MESSAGE_UPDATE
     * @param update обновление, полученное из чата
     */
    void consumePhotoMessageUpdates(Update update);
}
