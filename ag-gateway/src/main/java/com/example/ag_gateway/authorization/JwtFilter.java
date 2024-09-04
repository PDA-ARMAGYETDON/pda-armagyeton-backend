package com.example.ag_gateway.authorization;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@WebFilter(urlPatterns = "/api/**")
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final List<String> excludeUrls = Arrays.asList("/api/users/signup", "/api/users/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 URL을 가져와서 제외할 URL 목록과 비교
        String requestURI = request.getRequestURI();
        System.out.println("requestURI 가져옴 : " + requestURI);

        // login/sign up에 대해서는 필터링을 수행하지 않음
        if (excludeUrls.stream().anyMatch(requestURI::startsWith)) {
            System.out.println("login/signup에 대해서는 필터링을 수행하지 않음");
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        System.out.println("authorization 가져옴 : " + authorization);


        // authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("authorization이 없거나 Bearer로 시작하지 않음");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Authorization가 필요합니다.");

            return;
        }


        String token = authorization.substring(7);
        System.out.println("token 가져옴 : " + token);

        // 토큰 만료 검증 - 만료함
        if(jwtUtil.isExpired(token)) {
            System.out.println("토큰의 유효기간이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("토큰의 유효기간이 만료되었습니다.");

            return;
        }

        // FIXME; AOP로 처리를 어떻게 할 것인지 고민해보기
//        request.setAttribute("loginId", jwtUtil.getLoginId(token));
//        request.setAttribute("teamId", jwtUtil.getTeamId(token));
//        System.out.println("loginId, teamId 헤더에 설정 완료");

//        System.out.println(request.getAttribute("loginId"));
//        System.out.println(request.getAttribute("teamId"));

        filterChain.doFilter(request, response);
    }
}
