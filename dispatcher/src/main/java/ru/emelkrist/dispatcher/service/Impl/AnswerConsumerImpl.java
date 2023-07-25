package ru.emelkrist.dispatcher.service.Impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.emelkrist.dispatcher.controller.UpdateProcessor;
import ru.emelkrist.dispatcher.service.AnswerConsumer;
import static ru.emelkrist.model.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;
    public AnswerConsumerImpl(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }
    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
