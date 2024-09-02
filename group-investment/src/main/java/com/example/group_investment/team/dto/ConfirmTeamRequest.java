package com.example.group_investment.team.dto;

import com.example.group_investment.enums.TeamStatus;
import lombok.Getter;

@Getter
public class ConfirmTeamRequest {
    private TeamStatus status;
}
