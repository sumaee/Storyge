package com.example.project.security.handler;

import com.example.project.security.auth.TokenInfo;
import com.example.project.security.auth.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        clearAuthenticationAttributes(request);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to ");
            return;
        }
        // rest 안되서 직접 넣어줘야함
        // json파일(토큰)을 만들어서 넘겨줘야함
        // jwtUtil 헤더에 담어줄 때마다
        clearAuthenticationAttributes(request);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        ObjectMapper om = new ObjectMapper();
        String jsonStr = null;

        PrintWriter writer = response.getWriter();
        try {
            jsonStr = om.writeValueAsString(tokenInfo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        writer.print(jsonStr);
    }

}
