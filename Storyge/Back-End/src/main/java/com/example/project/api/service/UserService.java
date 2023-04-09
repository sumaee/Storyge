package com.example.project.api.service;

import com.example.project.db.entity.User;
import com.example.project.dto.response.UserResp;

import java.util.List;

public interface UserService {

    //유저 정보 수정
    void updateUser(Long userId, String nickname, String profileUrl);


    UserResp selectOneUser(Long userId);

    // 사용자 검색
    List<UserResp> searchUser(String nickname, Long userId);

    boolean checkNickname(String nickname);

    default UserResp toDto(User user) {
        return UserResp.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg()).build();

    }
}
