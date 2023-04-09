package com.example.project.db.repository.custom;

import com.example.project.dto.common.DailyEmotionStatistic;
import com.example.project.dto.common.EmotionStatistic;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface DiaryCustomRepository {

    Optional<DailyEmotionStatistic> dailyEmotionStatistic(Long userId, LocalDate date);

    List<EmotionStatistic> emotionStatistic(String period, LocalDate date, Long userId);
}
