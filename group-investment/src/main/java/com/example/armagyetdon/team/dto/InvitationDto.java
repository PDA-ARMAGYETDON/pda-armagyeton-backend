package com.example.armagyetdon.team.dto;

import com.example.armagyetdon.team.Invitation;
import com.example.armagyetdon.team.Team;
import lombok.Builder;

public class InvitationDto {
    private Team team;
    private String inviteCode;
    private String inviteUrl;

    @Builder
    public InvitationDto(Team team, String inviteCode, String inviteUrl) {
        this.team = team;
        this.inviteCode = inviteCode;
        this.inviteUrl = inviteUrl;
    }
}
