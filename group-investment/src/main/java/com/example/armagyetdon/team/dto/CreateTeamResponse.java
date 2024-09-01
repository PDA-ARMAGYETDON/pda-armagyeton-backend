package com.example.armagyetdon.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateTeamResponse {
    private String inviteCode;
    private String inviteUrl;

}
