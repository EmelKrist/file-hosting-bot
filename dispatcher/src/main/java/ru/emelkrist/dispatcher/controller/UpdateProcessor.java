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

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

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

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }
    private void setFileIsReceivedView(Update update){
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Файл получен! Обрабатывается...");
        setView(sendMessage);
    }
    private void setView(SendMessage message) {
        telegramBot.executeMessage(message);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileIsReceivedView(update);
    }
}
