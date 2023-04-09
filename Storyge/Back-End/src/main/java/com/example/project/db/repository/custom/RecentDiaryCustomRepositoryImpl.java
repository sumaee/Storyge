package com.example.project.db.repository.custom;

import com.example.project.db.entity.RecentDiary;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.project.db.entity.QFollow.follow;
import static com.example.project.db.entity.QRecentDiary.recentDiary;


@Repository
public class RecentDiaryCustomRepositoryImpl implements RecentDiaryCustomRepository {

    private final JPAQueryFactory query;

    public RecentDiaryCustomRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public List<RecentDiary> selectAllRecentDiaryByFollowing(Long userId) {


        List<RecentDiary> recentDiaryList = query
                .selectFrom(recentDiary)
                .where(
                        (recentDiary.userId.in(
                                JPAExpressions
                                        .select(follow.following)
                                        .from(follow)
                                        .where(follow.follower.eq(userId))
                        )).and(recentDiary.endsAt.after(LocalDateTime.now()))

                )
                .orderBy(recentDiary.endsAt.desc())
                .fetch();


        return recentDiaryList;
    }
}
