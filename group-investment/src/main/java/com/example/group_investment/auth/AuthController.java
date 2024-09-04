package com.example.group_investment.auth;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.auth.exception.AuthoErrorCode;
import com.example.group_investment.auth.exception.AuthoException;
import com.example.group_investment.auth.utils.JwtUtil;
import com.example.group_investment.member.MemberRepository;
import com.example.group_investment.user.User;
import com.example.group_investment.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Operation(summary = "팀 변경 시 => JWT 토큰 갱신")
    @PutMapping("/api/auth/{team}")
    public ResponseEntity<?> updateToken(@RequestAttribute("loginId") String loginId, @RequestAttribute("teamId") int teamId,
                                         @RequestHeader("Authorization") String auth, @PathVariable int team){
//    public ResponseEntity<?> updateToken(HttpServletRequest request,
//                                         @RequestHeader("Authorization") String auth, @PathVariable int team){


        String newJwtToken = authService.updateToken(auth.substring(7), team);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newJwtToken);

        return new ResponseEntity<>(
                new ApiResponse<>(200, true, "jwt 토큰이 새롭게 발급되었습니다.", null),
                headers,
                HttpStatus.OK);
    }

}
