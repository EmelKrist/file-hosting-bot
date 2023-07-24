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

    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
