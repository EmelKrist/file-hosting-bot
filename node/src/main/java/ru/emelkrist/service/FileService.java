package ru.emelkrist.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.emelkrist.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
}
