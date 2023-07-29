package ru.emelkrist.service;

import org.springframework.core.io.FileSystemResource;
import ru.emelkrist.entity.AppDocument;
import ru.emelkrist.entity.AppPhoto;
import ru.emelkrist.entity.BinaryContent;

import java.util.Optional;

public interface FileService {
    Optional<AppDocument> getDocument(String id);
    Optional<AppPhoto> getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);

}
