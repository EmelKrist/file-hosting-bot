package ru.emelkrist.dispatcher.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.emelkrist.dispatcher.config.BotConfig;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final UpdateProcessor updateProcessor;

    @Autowired
    public TelegramBot(BotConfig config, UpdateProcessor updateProcessor) {
        this.config = config;
        this.updateProcessor = updateProcessor;
    }

    /**
     * Метод для регистрации (внедрения) бота в класс для распределения сообщений.
     * Note: регистрирует бота после создания бина этого компонента.
     */
    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        // настраиваем имя бота
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        // настраиваем токен бота
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // обрабатываем полученное обновление
        updateProcessor.processUpdate(update);
    }

    /**
     * Метод для выполнения отправки сообщения ботом в чат пользователя.
     * @param message - сообщение, отправляемое ботом в чат.
     */
    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
