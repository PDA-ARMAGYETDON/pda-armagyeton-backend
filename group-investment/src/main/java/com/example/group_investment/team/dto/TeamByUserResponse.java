package com.example.group_investment.team.dto;

import com.example.group_investment.enums.TeamStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamByUserResponse {
    private int teamId;
    private TeamStatus status;

    @Builder
    public TeamByUserResponse(int teamId, TeamStatus status) {
        this.teamId = teamId;
        this.status = status;
    }

    public TeamByUserResponse() {

    }
}
