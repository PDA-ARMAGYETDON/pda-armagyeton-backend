package com.example.group_investment.user;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.user.dto.FcmTokenRequestDto;
import com.example.group_investment.user.dto.GetUserResponse;
import com.example.group_investment.user.dto.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/api/users/{id}")
    public ApiResponse<GetUserResponse> getUser(@PathVariable int id) {
        return new ApiResponse<>(200, true, "사용자를 조회했습니다.", userService.get(id));
    }

    @PostMapping("/api/users/signup")
    public ApiResponse<String> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return new ApiResponse<>(201, true, "회원가입에 성공했습니다.", null);
    }

    @PostMapping("/api/users/fcm/issue")
    @Operation(summary = "FCM 토큰 발급", description = "유저 ID 와 FCM 토큰을 받아 alarm 모듈에 전송")
    public ApiResponse<?> saveFcmToken(@RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        log.warn("fcmTokenRequestDto: {}", fcmTokenRequestDto);
        System.out.println("토큰 컨트롤러 시작");
        userService.saveFcmToken(fcmTokenRequestDto);

        return new ApiResponse<>(200, true, "토큰을 등록하였습니다.", null);
    }

}