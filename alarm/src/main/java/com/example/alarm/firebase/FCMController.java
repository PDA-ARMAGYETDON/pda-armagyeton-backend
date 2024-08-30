package com.example.alarm.firebase;

import com.example.alarm.firebase.dto.FcmTokenRequestDto;
import com.example.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class FCMController {

    private final FcmService fcmService;

    // UserResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.

    @PostMapping("/token")
    @Operation(summary = "토큰 등록", description = "클라이언트로부터 유저, 팀 정보를 받아와 채팅에 필요한 토큰을 등록합니다.")
    public ApiResponse getUser(@RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        fcmService.saveFcmToken(fcmTokenRequestDto);
        return new ApiResponse<>(200, true, "토큰을 등록하였습니다.", null);
    }


}