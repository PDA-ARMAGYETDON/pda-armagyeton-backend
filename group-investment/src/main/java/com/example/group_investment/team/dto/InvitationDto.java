package com.example.group_investment.team.dto;

import com.example.group_investment.team.Invitation;
import com.example.group_investment.team.Team;
import lombok.Builder;

public class InvitationDto {
    private Team team;
    private String inviteCode;

    @Builder
    public InvitationDto(Team team, String inviteCode) {
        this.team = team;
        this.inviteCode = inviteCode;
    }

    public Invitation toEntity() {
        return Invitation.builder()
                .team(this.team)
                .inviteCode(this.inviteCode)
                .build();
    }
}
