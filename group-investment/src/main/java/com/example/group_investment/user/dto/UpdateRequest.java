package com.example.group_investment.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
@Valid
public class UpdateRequest {
    private String name;
    @Email
    private String email;
    private String address;
    private String addressDetail;
}
