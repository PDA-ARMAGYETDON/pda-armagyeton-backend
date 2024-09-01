package com.example.armagyetdon.team.dto;

import com.example.armagyetdon.enums.Category;
import com.example.armagyetdon.enums.TeamStatus;
import com.example.armagyetdon.rule.dto.RuleDto;
import com.example.armagyetdon.team.Team;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class CreateTeamRequest {
    private String name;
    private Category category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private float prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private Date period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;

}
