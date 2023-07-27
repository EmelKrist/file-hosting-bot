package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    /**
     * Метод для обработки тектового сообщения,
     * полученного из обновления в чате бота
     * @param update обновление в чате бота
     */
    void processTextMessage(Update update);
    /**
     * Метод для обработки фото, полученного
     * из обновления в чате бота
     * @param update обновление в чате бота
     */
    void processPhotoMessage(Update update);
    /**
     * Метод для обработки документа, полученного
     * из обновления в чате бота
     * @param update обновление в чате бота
     */
    void processDocMessage(Update update);
}
