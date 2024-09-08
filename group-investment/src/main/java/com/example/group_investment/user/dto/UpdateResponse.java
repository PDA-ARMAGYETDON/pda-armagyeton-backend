package com.example.group_investment.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateResponse {
    private String name;
    private String email;
    private String address;
    private String addressDetail;
}
