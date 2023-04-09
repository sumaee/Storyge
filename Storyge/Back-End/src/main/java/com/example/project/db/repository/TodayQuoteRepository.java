package com.example.project.db.repository;

import com.example.project.db.entity.TodayQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayQuoteRepository extends JpaRepository<TodayQuote, Long> {
}
