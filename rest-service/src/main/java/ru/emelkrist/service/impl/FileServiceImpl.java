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
import ru.emelkrist.utils.CryptoTool;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final AppPhotoDAO appPhotoDAO;
    private  final AppDocumentDAO appDocumentDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppPhotoDAO appPhotoDAO, AppDocumentDAO appDocumentDAO, CryptoTool cryptoTool) {
        this.appPhotoDAO = appPhotoDAO;
        this.appDocumentDAO = appDocumentDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public Optional<AppDocument> getDocument(String docId) {
        var id = cryptoTool.idOf(docId);
        if (id == null) return Optional.empty();
        return appDocumentDAO.findById(id);
    }

    @Override
    public Optional<AppPhoto> getPhoto(String photoId) {
        var id = cryptoTool.idOf(photoId);
        if (id == null) return Optional.empty();
        return appPhotoDAO.findById(id);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            String tempFilename = UUID.randomUUID().toString();
            File temp = File.createTempFile(tempFilename, ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
    }
}
