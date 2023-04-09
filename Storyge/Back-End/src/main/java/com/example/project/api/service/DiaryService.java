package com.example.project.api.service;

import com.example.project.dto.common.DailyEmotionDto;
import com.example.project.dto.request.DiaryReq;
import com.example.project.dto.response.DiaryResp;
import com.example.project.dto.common.DiaryUpdateParam;
import com.example.project.dto.common.EmotionStatistic;
import com.example.project.db.entity.Diary;

import java.util.List;
import java.util.Optional;

public interface DiaryService {

    //C
    Optional<Long> insertDiary(Long userId, DiaryReq diaryDto);

    //R

    Optional<DiaryResp> selectOneDiary(Long diaryId);

    List<DiaryResp> selectAllDailyDiary(Long userId, String stringDate);

    int selectDiaryCount(Long userId);

    List<EmotionStatistic> selectEmotionStatistic(String period, String stringDate, Long userId);

    //U
    boolean updateDiary(Long userId, DiaryUpdateParam param);

    boolean updateScope(Long userId, Long diaryId, int scope);

    //D
    boolean deleteDiary(Long userId, Long diaryId);

    // DB-> 서버
    default DiaryResp toResponseDto(Diary diary) {
        return DiaryResp.builder()
                .diaryId(diary.getDiaryId())
                .userId(diary.getUser().getUserId())
                .emoticonName(diary.getEmoticonName())
                .diaryContent(diary.getDiaryContent())
                .scope(diary.getScope())
                .updateCnt(diary.getUpdateCnt())
                .analyzedResult(diary.getAnalyzedResult())
                .createdAt(diary.getCreatedAt())
                .build();
    }

    //서버 -> DB
    default Diary toEntity(DiaryReq diaryReq) {
        return Diary.builder()
                .userId(diaryReq.getUserId())
                .emoticonName(diaryReq.getEmoticonName())
                .diaryContent(diaryReq.getDiaryContent())
                .scope(diaryReq.getScope())
                .analyzedResult(diaryReq.getAnalyzedResult())
                .build();
    }

    default DailyEmotionDto toDailyEmotionDto(Diary diary) {
        return DailyEmotionDto.builder()
                .userId(diary.getUserId())
                .emoticonName(diary.getEmoticonName())
                .createdAt(diary.getCreatedAt().toLocalDate())
                .build();
    }
}
