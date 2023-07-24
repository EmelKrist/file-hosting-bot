package ru.emelkrist.dispatcher.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    /**
     * Метод для генерации отправляемого сообщения с заданным текстом.
     * @param update - обновление чата.
     * @param text - текст отправляемого сообщения.
     * @return - сгенерированный объект отправляемого сообщения.
     */
    public SendMessage generateSendMessageWithText(Update update, String text){
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }
}
