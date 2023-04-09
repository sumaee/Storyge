package com.example.project.api.service;

import com.example.project.dto.response.RecentDiaryResp;

import java.util.List;

public interface RecentDiaryService {
    void insertRecentDiary(Long user, Long diary); // 일기가 새로 올라오면 추가됨
    Boolean insertReadDiary(Long userId, Long diaryId); // 읽은 일기 표시

    List<RecentDiaryResp> selectAllRecentDiary(Long userId);

}