package com.example.project.api.controller;

import com.example.project.api.service.DiaryService;
import com.example.project.api.service.FollowService;
import com.example.project.api.service.RecentDiaryService;
import com.example.project.dto.common.DiaryUpdateParam;
import com.example.project.dto.common.EmotionStatistic;
import com.example.project.dto.request.DiaryReq;
import com.example.project.dto.response.DiaryResp;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Api(tags = {"다이어리 API"})
public class DiaryController {
    private static final String SUCCESS = "Success";
    private static final String FAIL = "Fail";
    private final DiaryService diaryService;
    private final RecentDiaryService recentDiaryService;
    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiOperation(value = "일기 작성", notes = "일기 작성하기")
    @PostMapping("/diary")
    public ResponseEntity<?> insertDiary(@RequestBody DiaryReq diaryReq,
                                         @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        diaryReq.setUserId(userId);
        Optional<Long> optionalDiaryId = diaryService.insertDiary(userId, diaryReq);
        if (optionalDiaryId.isPresent()) {
            return new ResponseEntity<>(optionalDiaryId.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(FAIL, HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "일기 1개 조회", notes = "일기 상세페이지에 들어갈 일기 하나를 조회한다 \ndiaryId : 1")
    @GetMapping("/diary/detail/{diaryId}")
    public ResponseEntity<?> selectOneDiary(@PathVariable Long diaryId,
                                            @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {

        Long userId = jwtTokenProvider.getId(accessToken);

        Optional<DiaryResp> diaryResponseDto = diaryService.selectOneDiary(diaryId);

        if (diaryResponseDto.isEmpty()) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        recentDiaryService.insertReadDiary(userId, diaryId);
        return new ResponseEntity<>(diaryResponseDto.get(), HttpStatus.OK);
    }

    @ApiOperation(value = "일별 일기 목록(본인)", notes = "선택한 날짜의 본인 일기들을 가져온다 \ndate : 2023-02-07")
    @GetMapping("/diary/daily/{date}")
    public ResponseEntity<?> selectAllMyDailyDiary(@PathVariable("date") String stringDate,
                                                   @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        List<DiaryResp> diaryRespList = diaryService.selectAllDailyDiary(userId, stringDate);
        if (diaryRespList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(diaryRespList, HttpStatus.OK);
    }

    @ApiOperation(value = "일별 일기 목록(타인)", notes = "선택한 날짜의 타인 일기들을 가져온다 \nuserId : 4 \ndate : 2023-02-07")
    @GetMapping("/diary/daily/{date}/{userId}")
    public ResponseEntity<?> selectAllDailyDiary(@PathVariable Long userId,
                                                 @PathVariable("date") String stringDate,
                                                 @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long myId = jwtTokenProvider.getId(accessToken);
        if (!followService.checkFollow(myId, userId)) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }

        List<DiaryResp> diaryRespList = diaryService.selectAllDailyDiary(userId, stringDate);
        if (diaryRespList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(diaryRespList, HttpStatus.OK);
    }

    @ApiOperation(value = "오늘 일기 작성 횟수", notes = "오늘 하루동안 작성한 일기 개수 반환")
    @GetMapping("/diary/count")
    public ResponseEntity<?> selectDiaryCount(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        int diaryCnt = diaryService.selectDiaryCount(userId);
        if (diaryCnt < 0) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(diaryCnt, HttpStatus.OK);
    }

    @ApiOperation(value = "본인의 년/월별 감정 통계", notes = "본인의 년/월별 감정 통계를 가져온다.\nperiod : (month or year) \ndate : 2023-02-07")
    @GetMapping("/diary/statistic/{period}/{date}")
    public ResponseEntity<?> selectMyEmotionStatistic(@PathVariable String period,
                                                      @PathVariable("date") String stringDate,
                                                      @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {

        Long userId = jwtTokenProvider.getId(accessToken);
        List<EmotionStatistic> emotionStatisticList = diaryService.selectEmotionStatistic(period, stringDate, userId);
        if (emotionStatisticList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(emotionStatisticList, HttpStatus.OK);
    }

    @ApiOperation(value = "타인의 년/월별 감정 통계", notes = "타인의 년/월별 감정 통계를 가져온다.\nperiod : (month => 월별, year => 년별) \ndate : 2023-02-07 \nuserId : 4")
    @GetMapping("/diary/statistic/{period}/{date}/{userId}")
    public ResponseEntity<?> selectEmotionStatistic(@PathVariable String period,
                                                    @PathVariable("date") String stringDate,
                                                    @PathVariable Long userId,
                                                    @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long myId = jwtTokenProvider.getId(accessToken);
        if (!followService.checkFollow(myId, userId)) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
//400이맞는거같아
        List<EmotionStatistic> emotionStatisticList = diaryService.selectEmotionStatistic(period, stringDate, userId);
        if (emotionStatisticList == null) {
            return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(emotionStatisticList, HttpStatus.OK);
    }

    @ApiOperation(value = "일기 수정", notes = "일기를 수정한다. 일기 수정 횟수가 0인 경우에만 가능하다")
    @PutMapping("/diary")
    public ResponseEntity<String> updateDiary(@RequestBody DiaryUpdateParam param,
                                              @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        if (diaryService.updateDiary(userId, param)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "일기 공개 여부 변경", notes = "일기 공개 여부를 변경한다\n diaryId : 1 \n scope : (1 => 공개, 0 => 비공개) ")
    @PutMapping("/diary/scope/{diaryId}/{scope}")
    public ResponseEntity<String> updateScope(@PathVariable Long diaryId,
                                              @PathVariable Integer scope,
                                              @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        if (diaryService.updateScope(userId, diaryId, scope)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "일기 삭제", notes = "선택한 일기를 삭제한다 \n diaryId : 1")
    @DeleteMapping("/diary/{diaryId}")
    public ResponseEntity<String> deleteDiary(@PathVariable Long diaryId,
                                              @CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        if (diaryService.deleteDiary(userId, diaryId)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        }
        return new ResponseEntity<>(FAIL, HttpStatus.NOT_FOUND);
    }
}
