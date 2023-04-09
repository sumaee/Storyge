package com.example.project.api.controller;

import com.example.project.api.service.NotificationService;
import com.example.project.dto.common.NotificationUpdateParam;
import com.example.project.dto.response.NotificationResp;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"알림 API"})
public class NotificationController {

    private static final String SUCCESS = "Success";
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    //알림 목록
    @ApiOperation(value = "알림 목록 조회", notes = "현재 로그인한 사용자의 알림을 30개 가져옴(최근 알림->오래된 알림 순서)\n" +
            "follow: 알림 보낸 사람\n" +
            "notiType: FOLLOW: 팔로우 수락, WAIT: 팔로우 신청, REVIEW: 댓글\n" +
            "isRead- 읽음:1, 안읽음:0")
    @GetMapping("/notification")
    public ResponseEntity<List<NotificationResp>> selectAllNotification(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        List<NotificationResp> notificationList = notificationService.selectAllNotification(userId);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }


    @ApiOperation(value = "알림 읽음 처리", notes = "notificationId를 담아서 보내면 읽음 처리\n" +
            "isRead-> 1로 보내야 읽음 처리")
    @PutMapping("/notification")
    public ResponseEntity<String> updateNotificationRead(@RequestBody NotificationUpdateParam updateParam) {

        notificationService.updateNotificationRead(updateParam);

        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }


}
