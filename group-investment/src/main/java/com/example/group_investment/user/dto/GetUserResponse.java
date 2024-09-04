package com.example.group_investment.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserResponse {
    private String loginId;

    private String email;

    private String name;

    private String address;
}
