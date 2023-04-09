package com.example.project.api.controller;

import com.example.project.api.service.ReviewService;
import com.example.project.dto.common.ReviewUpdateParam;
import com.example.project.dto.request.ReviewReq;
import com.example.project.dto.response.ReviewResp;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"다이어리의 댓글 API"})
public class ReviewController {

    private static final String SUCCESS = "Success";
    private static final String FAIL = "Fail";
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    // 댓글 입력
    @ApiOperation(value = "댓글 입력", notes = "다이어리에 댓글 달기")
    @PostMapping("/review")
    public ResponseEntity<String> insertReview(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                               @RequestBody @ApiParam(value = "댓글 입력시 필요한 정보") ReviewReq reviewReq) {

        Long userId = jwtTokenProvider.getId(accessToken);
        reviewReq.setUserId(userId);
        reviewService.insertReview(userId, reviewReq);
        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }

    // 다이어리에 해당하는 댓글 조회
    @ApiOperation(value = "댓글 목록 조회", notes = "diaryId를 통해 그 diary에 해당하는 댓글 목록을 조회한다")
    @GetMapping("/review/{diaryId}")
    public ResponseEntity<List<ReviewResp>> selectAllReview(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                            @PathVariable @ApiParam(value = "댓글 조회할 다이어리의 id(pk)", example = "0") Long diaryId) {

        Long userId = jwtTokenProvider.getId(accessToken);

        List<ReviewResp> reviewList = reviewService.selectAllReview(userId, diaryId);
        return new ResponseEntity<>(reviewList, HttpStatus.OK);
    }

    // 댓글 수정
    @ApiOperation(value = "댓글 수정", notes = "댓글 수정, 수정할 내용과 그 댓글의 id 필요")
    @PutMapping("/review")
    public ResponseEntity<String> updateReview(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                               @RequestBody @ApiParam(value = "댓글 수정시 필요한 정보") ReviewUpdateParam reviewUpdateParam) {

        Long userId = jwtTokenProvider.getId(accessToken);

        if (reviewService.updateReview(userId, reviewUpdateParam)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }

    // 댓글 삭제
    @ApiOperation(value = "댓글 삭제", notes = "댓글 삭제")
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                               @PathVariable @ApiParam(value = "삭제할 댓글의 id(pk)", example = "0") Long reviewId) {

        Long userId = jwtTokenProvider.getId(accessToken);

        if (reviewService.deleteReview(userId, reviewId)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }
}
