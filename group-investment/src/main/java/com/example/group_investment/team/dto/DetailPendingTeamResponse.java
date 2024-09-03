package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import com.example.group_investment.enums.RulePeriod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class DetailPendingTeamResponse {
    private String name;
    private int baseAmt;
    private int headCount;
    private Category category;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private double prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private RulePeriod period;
    private Date payDate;
    private double maxLossRt;
    private double maxProfitRt;
    private int invitedMembers;
    private int isLeader;
    private int isParticipating;

    @Builder
    public DetailPendingTeamResponse(String name, int baseAmt, int headCount, Category category, LocalDateTime startAt, LocalDateTime endAt, double prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, RulePeriod period, Date payDate, double maxLossRt, double maxProfitRt, int invitedMembers, int isLeader, int isParticipating) {
        this.name = name;
        this.baseAmt = baseAmt;
        this.headCount = headCount;
        this.category = category;
        this.startAt = startAt;
        this.endAt = endAt;
        this.prdyVrssRt = prdyVrssRt;
        this.urgentTradeUpvotes = urgentTradeUpvotes;
        this.tradeUpvotes = tradeUpvotes;
        this.depositAmt = depositAmt;
        this.period = period;
        this.payDate = payDate;
        this.maxLossRt = maxLossRt;
        this.maxProfitRt = maxProfitRt;
        this.invitedMembers = invitedMembers;
        this.isLeader = isLeader;
        this.isParticipating = isParticipating;
    }

    public DetailPendingTeamResponse() {

    }
}
