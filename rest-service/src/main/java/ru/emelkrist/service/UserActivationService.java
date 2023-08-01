package ru.emelkrist.service;

public interface UserActivationService {
    /**
     * Метод для активации аккаунта пользователя
     * @param cryptoUserId зашиврованный идентификатор пользователя
     * @return результат активации (true/false)
     */
    boolean activation(String cryptoUserId);
}
