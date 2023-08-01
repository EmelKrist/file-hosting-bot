package ru.emelkrist.service.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.emelkrist.dao.AppDocumentDAO;
import ru.emelkrist.dao.AppPhotoDAO;
import ru.emelkrist.dao.BinaryContentDAO;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;
import ru.emelkrist.entity.BinaryContent;
import ru.emelkrist.exceptions.UploadFileException;
import ru.emelkrist.service.FileService;
import ru.emelkrist.service.enums.LinkType;
import ru.emelkrist.utils.CryptoTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFileInfo(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument appDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(appDoc); // сохраняем построенный объект в БД (делаем persistent)
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        // Для получения качественного фото, нужно брать посленднее по индексу фото
        var lastIndexPhoto = telegramMessage.getPhoto().size() - 1;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(lastIndexPhoto);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFileInfo(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto appPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(appPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    /**
     * Метод для построения объекта фото (неотслеживаемого в БД)
     *
     * @param telegramPhoto           фото телеграма
     * @param persistentBinaryContent двоичнй контент фото из связанной таблицы в БД
     * @return объект фото приложения
     */
    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    /**
     * Метод для построения объекта дкумента (неотслеживаемого в БД)
     *
     * @param telegramDoc             документ телеграма
     * @param persistentBinaryContent двоичнй контент документа из связанной таблицы в БД
     * @return объект документа приложения
     */
    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    /**
     * Метод для скачивания файла в бинарном формете и сохранения этих данных в БД
     * Note: Получает путь файла, скачивает файл в виде потока байт,
     * создает объект для хранения потока байт, который не отслеживается БД,
     * после чего сохраняет его в ней, делая отслеживаемым (persistent)
     *
     * @param response ответ с информацией о файле
     * @return объект хранения двоичного контента
     */
    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    /**
     * Метод для скаивания файла из хранилища данных бота
     * в виде потока байт
     *
     * @param filePath путь к файлу в хранилище
     * @return массив байт файла
     */
    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);

        URL urlObj = null;
        try { // создаем URL по URI к файлу в харнилище бота
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }
        // в потоке для чтения получаем поток байт файла
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }

    }

    /**
     * Метод для получения(парсинга) пути файла в хранилище данных бота
     * из json с информацией о файле
     *
     * @param response ответ с информацией о файле
     * @return путь к файлу
     */
    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    /**
     * Метод для получения иформации о файле из телеграма
     *
     * @param fileId идентификатор файла
     * @return json с информацией о файле
     */
    private ResponseEntity<String> getFileInfo(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }


}
