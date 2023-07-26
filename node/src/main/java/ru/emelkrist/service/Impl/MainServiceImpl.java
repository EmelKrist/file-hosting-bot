package ru.emelkrist.service.Impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.dao.RowDataDAO;
import ru.emelkrist.entity.RawData;
import ru.emelkrist.service.MainService;
import ru.emelkrist.service.ProducerService;

@Service
public class MainServiceImpl implements MainService {

    private final RowDataDAO rowDataDAO;

    private final ProducerService producerService;

    public MainServiceImpl(RowDataDAO rowDataDAO, ProducerService producerService) {
        this.rowDataDAO = rowDataDAO;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRowData(update);
    }

    private void saveRowData(Update update) {
        RawData rawData = RawData.builder()
                        .event(update)
                        .build();
        rowDataDAO.save(rawData);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Hello from Node!");
        producerService.producerAnswer(sendMessage);
    }
}
