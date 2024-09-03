package com.example.armagyetdon.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequest {
    private String loginId;
    private String password;
    private String email;
    private String name;
    private String address;
}
