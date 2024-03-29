package com.example.project.api.controller;

import com.example.project.api.service.FollowService;
import com.example.project.dto.common.FollowUserInfoDto;
import com.example.project.dto.common.UserIdDto;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"팔로워, 팔로잉 관련 API"})
public class FollowController {

    private static final String SUCCESS = "Success";
    private static final String FAIL = "Fail";
    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    //팔로우 신청하기
    @ApiOperation(value = "팔로우 신청", notes = "상대방에게 팔로우 신청을 보낸다")
    @PostMapping("/follow-wait")
    public ResponseEntity<String> insertFollowWait(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                   @RequestBody @ApiParam(value = "신청할 사용자의 userId(pk)") UserIdDto userIdDto) {

        Long userId = jwtTokenProvider.getId(accessToken);

        if (followService.insertFollowWait(userId, userIdDto)) { // 신청 성공
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            // 신청 실패
            // -> 신청하려는 상대가 존재하지 않거나 이미 팔로우 신청 중이거나 팔로우 중
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }

    //팔로우 수락
    @ApiOperation(value = "팔로우 수락", notes = "나에게 들어온 팔로우를 수락한다")
    @PostMapping("/follow")
    public ResponseEntity<String> insertFollower(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                 @RequestBody @ApiParam(value = "수락할 사용자의 userId(pk)") UserIdDto userIdDto) {

        Long userId = jwtTokenProvider.getId(accessToken);

        if (followService.insertFollower(userId, userIdDto)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }

    }

    //팔로우 수락 대기 목록
    @ApiOperation(value = "팔로우 신청 대기 목록",
            notes = "나에게 팔로우 신청을 한 사람들의 목록 조회")
    @GetMapping("/follow-wait")
    public ResponseEntity<List<FollowUserInfoDto>> selectAllFollowWait(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {

        Long userId = jwtTokenProvider.getId(accessToken);

        List<FollowUserInfoDto> followWaitList = followService.selectAllFollowWait(userId);
        return new ResponseEntity<>(followWaitList, HttpStatus.OK);
    }


    //내 팔로잉 목록
    @ApiOperation(value = "내 팔로잉 목록",
            notes = "내가 팔로우 하는 사람들의 목록 조회")
    @GetMapping("/following")
    public ResponseEntity<List<FollowUserInfoDto>> selectAllFollowing(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {

        Long userId = jwtTokenProvider.getId(accessToken);

        List<FollowUserInfoDto> followingList = followService.selectAllFollowing(userId);
        return new ResponseEntity<>(followingList, HttpStatus.OK);
    }

    // 내 팔로워 목록
    @ApiOperation(value = "내 팔로워 목록",
            notes = "나를 팔로우 하는 사람들의 목록 조회")
    @GetMapping("/follower")
    public ResponseEntity<List<FollowUserInfoDto>> selectAllFollower(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {


        Long userId = jwtTokenProvider.getId(accessToken);

        List<FollowUserInfoDto> followerList = followService.selectAllFollower(userId);
        return new ResponseEntity<>(followerList, HttpStatus.OK);
    }

    // 팔로잉 확인
    @ApiOperation(value = "팔로잉 확인", notes = "내가 상대방을 팔로잉하는중인지 확인")
    @GetMapping("following/check/{userId}")
    public ResponseEntity<Boolean> checkFollow(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                               @PathVariable Long userId) {

        Long myId = jwtTokenProvider.getId(accessToken);

        Boolean result = followService.checkFollow(myId, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //팔로우 대기 삭제
    @ApiOperation(value = "팔로우 대기 삭제",
            notes = "나에게 들어온 팔로우 신청을 삭제한다(거절)")
    @ApiImplicitParam(name = "userId", value = "거절할 사용자의 userId(pk)", example = "0", dataTypeClass = Long.class)
    @DeleteMapping("/follow-wait/{userId}")
    public ResponseEntity<String> deleteFollowWait(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                   @PathVariable Long userId) {

        Long currentUser = jwtTokenProvider.getId(accessToken);

        if (followService.deleteFollowWait(currentUser, userId)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }

    // 팔로잉 삭제
    @ApiOperation(value = "팔로잉 삭제",
            notes = "팔로우를 취소함 - 내가 팔로우 하고 있는 사람 삭제")
    @ApiImplicitParam(name = "userId", value = "삭제할 사용자(팔로잉)의 userId(pk)", example = "0", dataTypeClass = Long.class)
    @DeleteMapping("/following/{userId}")
    public ResponseEntity<String> deleteFollowing(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                  @PathVariable Long userId) {

        Long currentUser = jwtTokenProvider.getId(accessToken);

        if (followService.deleteFollow(userId, currentUser)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }

    // 팔로워 삭제
    @ApiOperation(value = "팔로워 삭제",
            notes = "나를 팔로우 하고 있는 사람 삭제")
    @ApiImplicitParam(name = "userId", value = "삭제할 사용자(팔로워)의 userId(pk)", example = "0", dataTypeClass = Long.class)
    @DeleteMapping("/follower/{userId}")
    public ResponseEntity<String> deleteFollower(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                 @PathVariable Long userId) {

        Long currentUser = jwtTokenProvider.getId(accessToken);

        if (followService.deleteFollow(currentUser, userId)) {
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(FAIL, HttpStatus.NO_CONTENT);
        }
    }


}
