package com.example.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private final List<String> excludeUrls = Arrays.asList(
            "/api/users/signup", "/api/users/login",
            "/swagger-ui", "/v3/api-docs",
            "/api/group/backend", "/api/stock/backend",
            "/api/teams/autoPayment", "/api/teams/expelMember",
            "/api/users/valid"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String currentUri = request.getRequestURI();

        for (String uri : excludeUrls) {
            if (currentUri.startsWith(uri)) {
                return true;
            }
        }

        String authHeader = request.getHeader("Authorization");
        String jwtToken = jwtUtil.extractToken(authHeader);

        int userId = jwtUtil.getUserId(jwtToken);
        request.setAttribute("userId", userId);

        int teamId = 0;

        if (jwtUtil.containsTeam(jwtToken)) {
            teamId = jwtUtil.getTeamId(jwtToken);
        }
        request.setAttribute("teamId", teamId);

        log.info("[INTERCEPTOR] userId : " + userId + ", teamId : " + teamId);

        return true;
    }
}
