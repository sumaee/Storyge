package com.example.project.db.repository;

import com.example.project.db.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByDiaryIdOrderByCreatedAt(Long diaryId);

}
