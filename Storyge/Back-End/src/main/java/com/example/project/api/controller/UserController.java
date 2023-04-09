package com.example.project.api.controller;

import com.example.project.api.service.FollowService;
import com.example.project.api.service.UserService;
import com.example.project.dto.response.UserResp;
import com.example.project.security.auth.JwtProperties;
import com.example.project.security.auth.JwtTokenProvider;
import com.example.project.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
@Api(tags = {"사용자 관련 API"})
public class UserController {
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private final UserService userService;
    private final FollowService followService;
    private final FileUtil fileUtil;
    private final JwtTokenProvider jwtTokenProvider;


    @ApiOperation(value = "사용자 정보 수정", notes = "본인의 닉네임 또는 프로필 사진을 수정")
    @PutMapping(value = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserInfo(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                            @RequestPart(value = "multipartFile", required = false) MultipartFile multipartFile,
                                            @RequestPart(required = false) String nickname) throws IOException {
        Long userId = jwtTokenProvider.getId(accessToken);

        if (Objects.isNull(multipartFile) && Objects.isNull(nickname))
            return new ResponseEntity<>(FAIL, HttpStatus.OK);

        String url;
        if (Objects.isNull(multipartFile))
            url = null;
        else
            url = fileUtil.upload(multipartFile, "profile");

        userService.updateUser(userId, nickname, url);
        return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }

    //내 정보 불러오기 -> 이름, 프로필, 팔로워/팔로잉 수
    @ApiOperation(value = "본인 정보 불러오기", notes = "본인의 이름, 프로필, 팔로워/팔로잉 수 정보")
    @GetMapping("/user")
    public ResponseEntity<UserResp> selectOneUser(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken) {
        Long userId = jwtTokenProvider.getId(accessToken);
        return new ResponseEntity<>(userService.selectOneUser(userId), HttpStatus.OK);
    }

    //상대 정보 불러오기 -> 이름, 프로필, 팔로워/팔로잉 수
    @ApiOperation(value = "다른 유저 정보 불러오기", notes = "다른 유저의 이름, 프로필, 팔로워/팔로잉 수 정보")
    @GetMapping("/user/{otherId}")
    public ResponseEntity<Map<String, Object>> selectOtherUser(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                                               @PathVariable Long otherId) {
        Long userId = jwtTokenProvider.getId(accessToken);
        Map<String, Object> data = new HashMap<>();
        data.put("user", userService.selectOneUser(otherId));
        data.put("scope", followService.checkFollowWait(userId, otherId));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiOperation(value = "사용자를 닉네임으로 검색한다")
    @GetMapping("/user/search/{nickname}")
    public ResponseEntity<?> searchUser(@CookieValue(name = JwtProperties.ACCESS_TOKEN) String accessToken,
                                        @PathVariable String nickname) {
        Long userId = jwtTokenProvider.getId(accessToken);

        return new ResponseEntity<>(userService.searchUser(nickname, userId), HttpStatus.OK);
    }

    @ApiOperation(value = "사용자를 닉네임 중복 검사")
    @GetMapping("/user/check/{nickname}")
    public ResponseEntity<?> checkNickname(@PathVariable String nickname) {
        if (nickname.isEmpty())
            return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
        return new ResponseEntity<>(userService.checkNickname(nickname), HttpStatus.OK);
    }

}
