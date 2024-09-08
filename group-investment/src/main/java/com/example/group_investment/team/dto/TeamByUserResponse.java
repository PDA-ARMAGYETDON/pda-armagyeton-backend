package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.TeamStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TeamByUserResponse {
    private int teamId;
    private TeamStatus status;
    private String name;
    private Category category;

    @Builder
    public TeamByUserResponse(int teamId, TeamStatus status, String name, Category category) {
        this.teamId = teamId;
        this.status = status;
        this.name = name;
        this.category = category;
    }

    public TeamByUserResponse() {

    }
}
