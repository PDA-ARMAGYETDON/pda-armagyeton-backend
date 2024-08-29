package com.example.armagyetdon.user;

import com.example.armagyetdon.user.dto.LoginRequestDto;
import com.example.armagyetdon.user.dto.UserDto;
import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    // UserResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.
    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUser(@PathVariable String id) {

        return new ApiResponse<>(200, true, "사용자를 조회했습니다.", null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginRequestDto> login(@RequestBody LoginRequestDto loginRequest) {

        userService.login(loginRequest);
        return new ApiResponse<>(200, true, "로그인에 성공하였습니다.", null);
    }
}
