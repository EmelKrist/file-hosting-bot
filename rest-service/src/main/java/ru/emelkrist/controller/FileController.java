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

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var optionalAppDocument = fileService.getDocument(id);
        if (optionalAppDocument.isEmpty()) {
            // TODO Для формирования bad request добавить controllerAdvice
            return ResponseEntity.badRequest().build();
        }
        var doc = optionalAppDocument.get();
        var binaryContent = doc.getBinaryContent();

        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }


    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var optionalAppPhoto = fileService.getPhoto(id);
        if (optionalAppPhoto.isEmpty()) {
            // TODO Для формирования bad request добавить controllerAdvice
            return ResponseEntity.badRequest().build();
        }
        var photo = optionalAppPhoto.get();
        var binaryContent = photo.getBinaryContent();

        var fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }
}
