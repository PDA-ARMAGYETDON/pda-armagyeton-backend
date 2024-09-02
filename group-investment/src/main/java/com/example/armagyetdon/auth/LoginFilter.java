package com.example.armagyetdon.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AgUserDetailsService userDetailsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username;
        String password;

        try {
            if (request.getContentType().equalsIgnoreCase("application/json")) {
                // JSON 데이터 파싱
                System.out.println("application/json");
                Map<String, String> requestBody = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                username = requestBody.get("loginId");
                password = requestBody.get("password");
            } else {
                // x-www-form-urlencoded 방식의 폼 데이터 처리
                username = obtainUsername(request);
                password = obtainPassword(request);
            }
        } catch (IOException e) {
            throw new AuthenticationServiceException("Request content could not be read", e);
        }

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        AgUserDetails userDetails = (AgUserDetails) authentication.getPrincipal();

        String loginId = userDetails.getUsername();
//        Set<Integer> teams = userDetails.getTeams();
        boolean teams = userDetailsService.isTeamExist(userDetails.getId());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String token = jwtUtil.createJwt(loginId, role, teams, 60*60*10L);

        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 로그인 실패 시
        response.setStatus(401);
    }
}
