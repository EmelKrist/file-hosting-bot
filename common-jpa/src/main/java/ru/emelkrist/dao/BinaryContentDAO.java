package ru.emelkrist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.emelkrist.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
