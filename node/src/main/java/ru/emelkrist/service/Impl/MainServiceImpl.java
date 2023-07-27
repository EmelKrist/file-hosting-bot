package ru.emelkrist.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.emelkrist.dao.AppUserDAO;
import ru.emelkrist.dao.RawDataDAO;
import ru.emelkrist.entity.AppUser;
import ru.emelkrist.entity.RawData;
import ru.emelkrist.entity.enums.UserState;
import ru.emelkrist.service.MainService;
import ru.emelkrist.service.ProducerService;

import static ru.emelkrist.entity.enums.UserState.BASIC_STATE;
import static ru.emelkrist.entity.enums.UserState.WAIT_TO_EMAIL_STATE;
import static ru.emelkrist.service.enums.ServiceCommand.*;

@Service
@Slf4j
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;

    public MainServiceImpl(RawDataDAO rawDataDAO, AppUserDAO appUserDAO, ProducerService producerService) {
        this.rawDataDAO = rawDataDAO;
        this.appUserDAO = appUserDAO;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        if (CANCEL.equals(text)){
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)){
            output = processUserCommand(appUser, text);
        } else if (WAIT_TO_EMAIL_STATE.equals(userState)){
            //TODO добавить обработку почты
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова.";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        // TODO добавить сохранение документа
        var answer = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/get-doc/777";
        sendAnswer(answer, chatId);
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.isActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов";
            sendAnswer(error, chatId);
            return true;
        }

        return false;
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        // TODO добавить сохранение фото
        var answer = "Фото успешно загружено! Ссылка для скачивания: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private void sendAnswer(String output, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processUserCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)){
            // TODO добавить регистрацию
            return "Временно недоступно!";
        } else if (HELP.equals(cmd)){
            return help();
        } else if (START.equals(cmd)){
            return "Приветствую! Для просмотра доступных команд введите /help";
        } else {
            return "Неизвестная команда! Для просмотра доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n" +
                "/cancel - отмена выполнения текущей команды;\n"+
                "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());

        if (optional.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true)
                    // TODO после добавления регистрации изменить значение по умолчанию
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return optional.get();
    }

}
