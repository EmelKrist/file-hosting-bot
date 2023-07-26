package ru.emelkrist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emelkrist.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData , Long> {
}
