package com.example.project.api.service;

import com.example.project.dto.request.ReviewReq;
import com.example.project.dto.response.ReviewResp;
import com.example.project.dto.common.ReviewUpdateParam;
import com.example.project.db.entity.Review;

import java.util.List;
import java.util.Objects;

public interface ReviewService {

    void insertReview(Long userId, ReviewReq reviewDto);

    List<ReviewResp> selectAllReview(Long userId, Long diaryId);

    Boolean updateReview(Long userId, ReviewUpdateParam reviewUpdateParam);

    Boolean deleteReview(Long userId, Long reviewId);

    default ReviewResp toDto(Review review, Long userId) {
        return ReviewResp.builder()
                .reviewId(review.getReviewId()) // 댓글 id
                .reviewContent(review.getReviewContent()) // 댓글 내용
                .userId(review.getUser().getUserId()) // 댓글 작성한 사람 id
                .isMe(Objects.equals(userId, review.getUser().getUserId())) // 나인지 확인한다
                .nickname(review.getUser().getNickname()) // 댓글 작성한 사람 닉네임
                .profileImg(review.getUser().getProfileImg()) // 댓글 작성한 사람 프로필 이미지
                .createdAt(review.getCreatedAt()) // 댓글 작성 시간 yy.MM.dd HH:mm
                .build();
    }

    default Review toEntity(ReviewReq reviewReq) {
        return Review.builder()
                .reviewContent(reviewReq.getReviewContent())
                .diaryId(reviewReq.getDiaryId())
                .userId(reviewReq.getUserId())
                .build();
    }

}
