package com.example.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${spring.ag.url}")
    private String clientUrl;

    private final JwtUtil jwtUtil;

    private final List<String> excludeUrls = Arrays.asList(
            "/api/users/signup", "/api/users/login",
            "/swagger-ui", "/v3/api-docs",
            "/api/group/backend", "/api/stock/backend",
            "/api/teams/autoPayment", "/api/teams/expelMember",
            "/api/users/valid",
            "/api/auth/health-check", "/api/chat/health-check", "/api/stocks/health-check"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", clientUrl);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            return true;
        }

        String currentUri = request.getRequestURI();

        for (String uri : excludeUrls) {
            if (currentUri.startsWith(uri)) {
                return true;
            }
        }
        try {
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

        } catch (NullPointerException e) {
            log.error("[INTERCEPTOR] JWT Token is null");
            response.sendError(401, "JWT Token is null");
            return false;
        }

        return true;
    }
}
