package com.example.group_investment.team.dto;

import com.example.group_investment.team.Invitation;
import com.example.group_investment.team.Team;
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

    public Invitation toEntity() {
        return Invitation.builder()
                .team(this.team)
                .inviteCode(this.inviteCode)
                .inviteUrl(this.inviteUrl)
                .build();
    }
}
