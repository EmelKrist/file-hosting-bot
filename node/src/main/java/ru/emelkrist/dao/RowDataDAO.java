package ru.emelkrist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emelkrist.entity.RawData;

public interface RowDataDAO extends JpaRepository<RawData , Long> {
}
