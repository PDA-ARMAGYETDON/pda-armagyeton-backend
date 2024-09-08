package com.example.group_investment.auth.filter;

import com.example.common.auth.JwtUtil;
import com.example.group_investment.auth.AgUserDetails;
import com.example.group_investment.auth.AgUserDetailsService;
import com.example.group_investment.auth.exception.AuthoErrorCode;
import com.example.group_investment.auth.exception.AuthoException;
import com.example.group_investment.user.User;
import com.example.group_investment.user.exception.UserErrorCode;
import com.example.group_investment.user.exception.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AgUserDetailsService userDetailsService;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AgUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username;
        String password;

        try {
            log.info("[LoginFilter에서 로그인 시도] "+ request.getContentType());
            if (request.getContentType().startsWith("application/json")) {
                // JSON 데이터 파싱
                System.out.println("application/json");
                Map<String, String> requestBody = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                username = requestBody.get("loginId");
                password = requestBody.get("password");

                if (!userDetailsService.isUserActive(username)) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().write("탈퇴한 사용자입니다. Canceled User.");
                    response.getWriter().flush();
                    return null;
                }

            } else {
                // x-www-form-urlencoded 방식의 폼 데이터 처리
                username = obtainUsername(request);
                password = obtainPassword(request);
            }
        } catch (IOException e) {
            throw new AuthoException(AuthoErrorCode.IO_EXCEPTION);
        }

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {

            throw new AuthoException(AuthoErrorCode.LOGIN_FAILED);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        AgUserDetails userDetails = (AgUserDetails) authentication.getPrincipal();

        int userId = userDetails.getUserId();
        boolean isTeamExist = userDetailsService.isTeamExist(userId);
        Integer teamId = isTeamExist ? userDetailsService.getTeamId(userId) : null;

        String token = jwtUtil.createJwt(userId, teamId, isTeamExist);

        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 로그인 실패 시
        response.setStatus(401);
        throw new AuthoException(AuthoErrorCode.LOGIN_FAILED);
    }
}
