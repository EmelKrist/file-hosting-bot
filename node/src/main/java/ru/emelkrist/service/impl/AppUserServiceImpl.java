package ru.emelkrist.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.emelkrist.dao.AppUserDAO;
import ru.emelkrist.dto.MailParams;
import ru.emelkrist.entity.AppUser;
import ru.emelkrist.entity.enums.UserState;
import ru.emelkrist.service.AppUserService;
import ru.emelkrist.utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
@Slf4j
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) { // если акк. уже активен
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) { // если почта уже установлена
            return "Вам на почту уже было отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        // иначе устанавливаем состояние в ожидании почты для подтверждения
        appUser.setState(UserState.WAIT_TO_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, ваш email.";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try { // валидаируем введенный пользоватлем адресс
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel.";
        }
        var optional = appUserDAO.findByEmail(email);
        if (optional.isEmpty()) { // если такая почта еще не используется (нет в БД)
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            var response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) { // если отправка письма не удалась
                var msg = String.format("Отправка эл. письма на почту %s не удалась.", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return msg;
            }
            return "Вам на почту было отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже используется. Введите корректный email. " +
                    "Для отмены команды введите /cancel.";
        }
    }

    /**
     * Метод для отправки запроса в почтовый сервис (для отправки письма поддтверждения регистрации)
     * @param cryptoUserId зашифрованный идентификатор пользователя
     * @param email почта для отправки письма
     * @return ответ со статусом результата обработки запроса
     */
    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // формируем данные для письма (json)
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        // отправляем POST запрос на отправку письма активации
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
