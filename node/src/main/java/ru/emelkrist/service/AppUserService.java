package ru.emelkrist.service;

import ru.emelkrist.entity.AppUser;

public interface AppUserService {
    /**
     * Метод для регистрации пользователя
     * @param appUser польователь
     * @return ответ с результатом регистрации
     */
    String registerUser(AppUser appUser);

    /**
     * Метод для валидации, установки почты пользователю
     * и отправки сообщения для подтверждения регистрации
     * @param appUser пользователь
     * @param email почта
     * @return ответ с результатом подтверждения регистрации(почты)
     */
    String setEmail(AppUser appUser, String email);
}
