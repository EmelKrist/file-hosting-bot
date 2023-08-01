package ru.emelkrist.utils;

import org.hashids.Hashids;

/**
 * Класс для шифрования идентификаторов
 */
public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool(String salt) {
        // создаем объект шифрования, задавая мин. длину хэша и соль
        var minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    /**
     * Метод для шифрования значения
     * @param value значение
     * @return хэш значения
     */
    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    /**
     * Метод для дешифровки идентификатора
     * @param value хэшированное значение
     * @return идентификатор
     */
    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
