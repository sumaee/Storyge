package com.example.project.api.controller;

import com.example.project.api.service.DailyEmotionService;
import com.example.project.api.service.FollowService;
import com.example.project.dto.common.DailyEmotionDto;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"캘린더 API"})
public class DailyEmotionController {
    private static final String FAIL = "Fail";
    private final DailyEmotionService dailyEmotionService;
    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "본인 캘린더 일일 감정 조회", notes = "캘린더에 들어갈 본인의 일일 감정을 조회한다\ndate : yyyy-mm-dd")
    @GetMapping("/daily/{date}")
    public ResponseEntity<?> selectMyDailyEmotion(@PathVariable("date") String stringDate,
                                                  @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        List<DailyEmotionDto> dailyEmotionDtoList = dailyEmotionService.selectAllDailyEmotion(userId, stringDate);
        if (dailyEmotionDtoList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dailyEmotionDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "타인 캘린더 일일 감정 조회", notes = "캘린더에 들어갈 타인의 일일 감정을 조회한다\ndate : yyyy-mm-dd\nuserId : 4")
    @GetMapping("/daily/{date}/{userId}")
    public ResponseEntity<?> selectDailyEmotion(@PathVariable("date") String stringDate,
                                                @PathVariable Long userId,
                                                @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long myId = jwtTokenProvider.getId(accessToken);
        if (!followService.checkFollow(myId, userId)) {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }

        List<DailyEmotionDto> dailyEmotionDtoList = dailyEmotionService.selectAllDailyEmotion(userId, stringDate);
        if (dailyEmotionDtoList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(dailyEmotionDtoList, HttpStatus.OK);
    }

}
