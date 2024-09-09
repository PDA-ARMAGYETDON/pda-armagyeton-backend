package com.example.group_investment.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateTokenResponse {
    String jwtToken;
}
