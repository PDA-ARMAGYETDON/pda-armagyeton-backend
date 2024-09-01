package com.example.armagyetdon.user;

import com.example.armagyetdon.user.dto.GetUserResponse;
import com.example.armagyetdon.user.dto.SignInRequest;
import com.example.armagyetdon.user.dto.SignInResponse;
import com.example.armagyetdon.user.dto.SignUpRequest;
import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/users/{id}")
    public ApiResponse<GetUserResponse> getUser(@PathVariable int id) {
        return new ApiResponse<>(200, true, "사용자를 조회했습니다.", userService.get(id));
    }

    @PostMapping("/api/users/signup")
    public ApiResponse<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return new ApiResponse<>(200, true, "회원가입에 성공했습니다.", null);
    }

//    @GetMapping("/api/users/signin")
//    public ApiResponse<SignInResponse> signIn(@RequestBody SignInRequest request) {
//        return new ApiResponse<>(200, true, "사용자를 조회했습니다.", userService.signIn(request));
//    }
}
