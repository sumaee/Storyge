package com.example.project.api.service;

import com.example.project.dto.common.NotificationFollowDto;
import com.example.project.dto.response.NotificationResp;
import com.example.project.dto.common.NotificationReviewDto;
import com.example.project.dto.common.NotificationUpdateParam;

import java.util.List;

public interface NotificationService {

    // 팔로우 신청 알림
    void insertFollowWaitNotification(NotificationFollowDto notificationFollowDto);

    // 팔로우 수락 알림
    void insertFollowNotification(NotificationFollowDto notificationFollowDto);

    // 댓글 알림
    void insertReviewNotification(NotificationReviewDto notificationReviewDto);

    // 알림 목록 가져오기
    List<NotificationResp> selectAllNotification(Long userId);

    //읽음
    void updateNotificationRead(NotificationUpdateParam updateParam);

}
