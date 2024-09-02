package com.example.group_investment.team.dto;

import com.example.group_investment.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class DetailPendingTeamResponse {
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
    private int invitedMembers;
    private int isLeader;
    private int isParticipating;

    @Builder
    public DetailPendingTeamResponse(String name, Category category, LocalDateTime startAt, LocalDateTime endAt, float prdyVrssRt, int urgentTradeUpvotes, int tradeUpvotes, int depositAmt, Date period, Date payDate, float maxLossRt, float maxProfitRt, int invitedMembers, int isLeader, int isParticipating) {
        this.name = name;
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
