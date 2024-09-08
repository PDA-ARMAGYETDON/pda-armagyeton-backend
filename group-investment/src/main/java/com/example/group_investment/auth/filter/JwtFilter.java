package com.example.group_investment.auth.filter;

import com.example.common.auth.JwtUtil;
import com.example.group_investment.auth.AgUserDetails;
import com.example.group_investment.auth.exception.AuthoErrorCode;
import com.example.group_investment.auth.exception.AuthoException;
import com.example.group_investment.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> excludeUrls = Arrays.asList(
            "/api/users/signup", "/api/users/login", "/api/users/valid",
            "/swagger-ui", "/v3/api-docs",
            "/api/group/backend",
            "/api/users/valid"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 URL을 가져와서 제외할 URL 목록과 비교
        String requestURI = request.getRequestURI();
        log.info("Request URI 출력 : " + requestURI);

        // login/sign up/swagger에 대해서는 필터링을 수행하지 않음
        if (excludeUrls.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization Header not found");

            throw new AuthoException(AuthoErrorCode.UNAUTHORIZATION);
        }

        String token = authorization.substring(7);

        // 토큰 만료 검증 - 만료함
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            throw new AuthoException(AuthoErrorCode.EXPIRED_JWT_TOKEN);
        }

        User user = User.builder()
                .id(jwtUtil.getUserId(token))
                .build();

        // 유저 정보를 가지고 인증 객체 생성
        AgUserDetails userDetails = new AgUserDetails(user);

        // Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Security Context(세션)에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

}
