package com.example.project.db.repository;

import com.example.project.db.entity.DailyEmotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyEmotionRepository extends JpaRepository<DailyEmotion, Long> {

    Optional<DailyEmotion> findByUser_UserIdAndCreatedAt(Long userId, LocalDate createdAt);

    List<DailyEmotion> findAllByUser_UserIdAndCreatedAtBetween(Long userId, LocalDate firstOfMonth, LocalDate lastOfMonth);

}
