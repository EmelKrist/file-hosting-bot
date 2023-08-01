package ru.emelkrist.service;

import org.springframework.core.io.FileSystemResource;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;
import ru.emelkrist.entity.BinaryContent;

import java.util.Optional;

public interface FileService {
    /**
     * Метод для получения документа по id из БД
     * @param id зашифрованный идентификатор документа
     * @return объект документа (если найден)
     */
    Optional<AppDocument> getDocument(String id);

    /**
     * Метод для получения фото по id из БД
     * @param id зашифрованный идентификатор фото
     * @return объект фото (если найден)
     */
    Optional<AppPhoto> getPhoto(String id);

    /**
     * Метод для создания файлового ресурса из двоичного контента
     * @param binaryContent двоичный контент
     * @return файловый ресурс для работы с файлами
     */
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);

}
