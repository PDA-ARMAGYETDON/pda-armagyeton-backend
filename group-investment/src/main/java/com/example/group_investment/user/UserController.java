package com.example.group_investment.user;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회. jwt 토큰의 userId와 pathVariable의 User id가 일치해야 합니다.")
    @GetMapping("/api/users/{id}")
    public ApiResponse<GetUserResponse> getUser(@RequestAttribute("userId") int userId, @PathVariable int id) {
        return new ApiResponse<>(200, true, "사용자를 조회했습니다.", userService.get(userId, id));
    }

    @Operation(summary = "회원가입 (Authorization 필요 없음)")
    @PostMapping("/api/users/signup")
    public ApiResponse<String> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return new ApiResponse<>(201, true, "회원가입에 성공했습니다.", null);
    }

    @Operation(summary = "아이디 중복 체크 (Authorization 필요 없음)")
    @PostMapping("/api/users/valid/id")
    public ApiResponse<Boolean> checkId(@RequestBody ValidIdRequest request) {
        userService.checkId(request.getLoginId());
        return new ApiResponse<>(200, true, "사용 가능한 ID 입니다.", null);
    }

    @Operation(summary = "이메일 중복 체크 (Authorization 필요 없음)")
    @PostMapping("/api/users/valid/email")
    public ApiResponse<Boolean> checkEmail(@RequestBody ValidEmailRequest request) {
        userService.checkEmail(request.getEmail());
        return new ApiResponse<>(200, true, "사용 가능한 이메일 입니다.", null);
    }

    @PostMapping("/api/users/fcm/issue")
    @Operation(summary = "FCM 토큰 발급", description = "유저 ID 와 FCM 토큰을 받아 alarm 모듈에 전송")
    public ApiResponse<?> saveFcmToken(@RequestBody FcmTokenRequestDto fcmTokenRequestDto) {

        userService.saveFcmToken(fcmTokenRequestDto);
        
        return new ApiResponse<>(200, true, "토큰을 등록하였습니다.", null);
    }

}