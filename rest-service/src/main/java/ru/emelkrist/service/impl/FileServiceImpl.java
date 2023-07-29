package ru.emelkrist.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import ru.emelkrist.dao.AppDocumentDAO;
import ru.emelkrist.dao.AppPhotoDAO;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;
import ru.emelkrist.entity.BinaryContent;
import ru.emelkrist.service.FileService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final AppPhotoDAO appPhotoDAO;
    private  final AppDocumentDAO appDocumentDAO;

    public FileServiceImpl(AppPhotoDAO appPhotoDAO, AppDocumentDAO appDocumentDAO) {
        this.appPhotoDAO = appPhotoDAO;
        this.appDocumentDAO = appDocumentDAO;
    }

    @Override
    public Optional<AppDocument> getDocument(String docId) {
        // TODO Добавить дешифровку хеш-строки
        var id = Long.parseLong(docId);
        return appDocumentDAO.findById(id);
    }

    @Override
    public Optional<AppPhoto> getPhoto(String photoId) {
        // TODO Добавить дешифровку хеш-строки
        var id = Long.parseLong(photoId);
        return appPhotoDAO.findById(id);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            // TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }
}
