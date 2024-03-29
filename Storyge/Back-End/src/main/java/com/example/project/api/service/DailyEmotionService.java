package com.example.project.api.service;

import com.example.project.dto.common.DailyEmotionDto;
import com.example.project.db.entity.DailyEmotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyEmotionService {

    //C
    boolean insertDailyEmotion(DailyEmotionDto dailyEmotionDto);

    //R
    Optional<DailyEmotion> selectOneDailyEmotion(Long userId, LocalDate date);

    List<DailyEmotionDto> selectAllDailyEmotion(Long userId, String stringDate);

    //U
    void updateDailyEmotion(Long userId, LocalDate date, String emoticonName);

    //D
    void deleteDailyEmotion(Long userId, LocalDate date);

    // DB-> 서버
    default DailyEmotionDto toDto(DailyEmotion dailyEmotion) {
        return DailyEmotionDto.builder()
                .dailyId(dailyEmotion.getDailyId())
                .userId(dailyEmotion.getUserId())
                .emoticonName(dailyEmotion.getEmoticonName())
                .createdAt(dailyEmotion.getCreatedAt())
                .build();
    }

    //서버 -> DB
    default DailyEmotion toEntity(DailyEmotionDto dailyEmotionDto) {
        return DailyEmotion.builder()
                .dailyId(dailyEmotionDto.getDailyId())
                .userId(dailyEmotionDto.getUserId())
                .emoticonName(dailyEmotionDto.getEmoticonName())
                .createdAt(dailyEmotionDto.getCreatedAt())
                .build();
    }
}
