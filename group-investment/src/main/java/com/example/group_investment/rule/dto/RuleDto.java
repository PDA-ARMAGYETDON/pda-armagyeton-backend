package com.example.group_investment.rule.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class RuleDto {
    private float prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private Date period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;
}
