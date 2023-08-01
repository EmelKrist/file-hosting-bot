package ru.emelkrist.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.service.FileService;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Метод для обработки GET запроса на получение(скачивание) документа по его
     * идентификатору
     * @param id зашифрованный идентификатор
     * @return сущность ответа
     */
    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var optionalAppDocument = fileService.getDocument(id);
        if (optionalAppDocument.isEmpty()) { // если документа нет в БД
            // TODO Для формирования bad request добавить controllerAdvice
            return ResponseEntity.badRequest().build();
        }
        var doc = optionalAppDocument.get(); // получаем документ и двоичный контент
        var binaryContent = doc.getBinaryContent();
        // получаем файловый ресурс документа
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        } // если ресурс получен, отправляем его в ответ для скачивания документа в браузере
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }


    /**
     * Метод для обработки GET запроса на получение(скачивание) фото по его идентификатору
     * @param id зашифрованный идентификатор фото
     * @return сущность ответа
     */
    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var optionalAppPhoto = fileService.getPhoto(id);
        if (optionalAppPhoto.isEmpty()) { // если фото нет в БД
            // TODO Для формирования bad request добавить controllerAdvice
            return ResponseEntity.badRequest().build();
        }
        var photo = optionalAppPhoto.get(); // получаем фото и двоичный контент
        var binaryContent = photo.getBinaryContent();
        // получаем файловый ресурс фото
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        } // если ресурс получен, отправляем его в ответ для скачивания фото в браузере
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}
