package com.example.armagyetdon.user.dto;

import jakarta.validation.constraints.Email;

public class UserDto {
    private String loginId;

    @Email
    private String email;

    private String name;

    private String address;
}
