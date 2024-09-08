package com.example.group_investment.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateTeamResponse {
    private String inviteCode;
    private String inviteUrl;
    private String updatedToken;

}
