package ru.emelkrist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emelkrist.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
