package com.example.group_investment.auth;

import com.example.group_investment.auth.exception.AuthoErrorCode;
import com.example.group_investment.auth.exception.AuthoException;
import com.example.group_investment.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final List<String> excludeUrls = Arrays.asList("/api/users/signup", "/api/users/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 URL을 가져와서 제외할 URL 목록과 비교
        String requestURI = request.getRequestURI();

        // login/sign up에 대해서는 필터링을 수행하지 않음
        if (excludeUrls.stream().anyMatch(requestURI::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization Header not found");

            throw new AuthoException(AuthoErrorCode.UNAUTHORIZATION);
        }

        String token = authorization.substring(7);

        // 토큰 만료 검증
        if(jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            throw new AuthoException(AuthoErrorCode.EXPIRED_JWT_TOKEN);
        }

        User user = User.builder()
                .loginId(jwtUtil.getLoginId(token))
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
