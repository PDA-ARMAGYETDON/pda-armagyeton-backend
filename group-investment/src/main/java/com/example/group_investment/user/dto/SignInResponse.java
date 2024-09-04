package com.example.group_investment.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {
    private String accessToken;
//    private String refreshToken;
}
