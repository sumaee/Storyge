package com.example.project.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "토큰 재발행, 로그아웃 시 사용할 정보", description = "토큰의 정보가 담김")
public class TokenReq {
    private String accessToken;
    private String refreshToken;


}
