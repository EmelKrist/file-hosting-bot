package ru.emelkrist.dispatcher.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.dispatcher.service.UpdateProducer;
import ru.emelkrist.dispatcher.utils.MessageUtils;

import static ru.emelkrist.model.RabbitQueue.*;

@Controller
@Slf4j
public class UpdateProcessor {

    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private UpdateProducer updateProducer;

    public UpdateProcessor(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
        this.messageUtils = messageUtils;
    }

    /**
     * Метод для внедрения объекта телеграм бота.
     * Note: Нужно по причине невозможности внедрения средствами Spring,
     * так как это приведет к замкнутому кругу (приложение не запустится).
     * @param telegramBot - объект телеграм бота.
     */
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Метод для обработки обновления чата.
     * Если обновление пустое, то выводит ошибку в логи
     * Если обновление имеет сообщение, то распределяет его по типу,
     * иначе выводит ошибку о неподдерживаемом типе сообщения.
     * @param update - обновление чата.
     */
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type is received" + update);
        }
    }

    /**
     *
     * @param update - обновление чата.
     */
    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();

        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    /**
     * Метод для установки представления сообщения о неподдерживаемом типе сообщения.
     * @param update - обновление чата.
     */
    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    /**
     * Метод для установки представления сообщения об успешном получении файла
     * и начала обработки.
     * @param update - обновление чата.
     */
    private void setFileIsReceivedView(Update update){
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Файл получен! Обрабатывается...");
        setView(sendMessage);
    }

    /**
     * Метод для установки представления (отправки сообщения от бота в чат).
     * @param message - отправляемое сообщение.
     */
    private void setView(SendMessage message) {
        telegramBot.executeMessage(message);
    }

    /**
     * Метод для обработки текстового сообщения.
     * @param update - обновление чата.
     */
    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    /**
     * Метод для обработки сообщения с документом.
     * @param update - обновление чата.
     */
    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    /**
     * Метод для обработки сообщения с фотографией.
     * @param update - обновление чата.
     */
    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
}
