package com.example.project.db.repository;

import com.example.project.db.entity.DiaryCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCountRepository extends JpaRepository<DiaryCount, Long> {
}
