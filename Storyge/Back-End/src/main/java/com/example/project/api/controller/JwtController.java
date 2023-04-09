package com.example.project.api.controller;

import com.example.project.api.service.JwtService;
import com.example.project.security.auth.TokenInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Api(tags = {"토큰 재발급"})
@RequestMapping("/tokens")
public class JwtController {

    private final JwtService jwtService;

    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenInfo tokenInfo = jwtService.reissue(request);
        response.addHeader("Set-Cookie", tokenInfo.generateAccessToken().toString());
        response.addHeader("Set-Cookie", tokenInfo.generateRefreshToken().toString());
        return new ResponseEntity<>("재발급 완료", HttpStatus.OK);
    }

}