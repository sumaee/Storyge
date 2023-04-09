package com.example.project.db.repository;

import com.example.project.db.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByUser_UserIdAndCreatedAtBetween(Long userId, LocalDateTime startTimeOfDay, LocalDateTime endTimeOfDay);
}
