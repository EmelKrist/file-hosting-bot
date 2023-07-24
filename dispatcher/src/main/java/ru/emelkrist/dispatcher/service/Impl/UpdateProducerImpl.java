package ru.emelkrist.dispatcher.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.emelkrist.dispatcher.service.UpdateProducer;

@Slf4j
@Service
public class UpdateProducerImpl implements UpdateProducer {
    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug(update.getMessage().getText());
    }
}
