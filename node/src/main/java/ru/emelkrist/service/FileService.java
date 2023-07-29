package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
