package com.example.project.api.service;

import com.example.project.security.auth.TokenInfo;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {
    TokenInfo reissue(HttpServletRequest request);

}
