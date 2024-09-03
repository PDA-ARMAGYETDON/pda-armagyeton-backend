package com.example.group_investment.user;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.user.dto.GetUserResponse;
import com.example.group_investment.user.dto.SignUpRequest;
import jakarta.validation.Valid;
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
    public ApiResponse<String> signUp( @RequestBody @Valid SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
        return new ApiResponse<>(201, true, "회원가입에 성공했습니다.", null);
    }
}
