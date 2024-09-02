package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.RulePeriod;
import com.example.group_investment.enums.TeamStatus;
import com.example.group_investment.rule.dto.RuleDto;
import com.example.group_investment.team.Team;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class CreateTeamRequest {
    private String name;
    private int baseAmt;
    private int headCount;
    private Category category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private float prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private RulePeriod period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;

}
