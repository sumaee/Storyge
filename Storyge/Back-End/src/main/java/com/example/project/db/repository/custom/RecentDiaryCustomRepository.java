package com.example.project.db.repository.custom;

import com.example.project.db.entity.RecentDiary;

import java.util.List;

public interface RecentDiaryCustomRepository {

    List<RecentDiary> selectAllRecentDiaryByFollowing(Long userId);
}
