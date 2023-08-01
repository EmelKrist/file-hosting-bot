package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;
import ru.emelkrist.service.enums.LinkType;

public interface FileService {
    /**
     * Метод для обработки документов
     * @param telegramMessage сообщение с документом
     * @return объект полученного документа
     */
    AppDocument processDoc(Message telegramMessage);
    /**
     * Метод для обработки фото
     * @param telegramMessage сообщение с фото
     * @return объект полученного фото
     */
    AppPhoto processPhoto(Message telegramMessage);

    /**
     * Метод генерации ссылки для скачивания файла
     * @param docId идентификатор файла
     * @param linkType тип ссылки (для фото или документа)
     * @return ссылка
     */
    String generateLink(Long docId, LinkType linkType);
}
