package com.example.armagyetdon.rule.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class CreateRuleRequest {
    private float prdyVrssRt;
    private int urgentTradeUpvotes;
    private int tradeUpvotes;
    private int depositAmt;
    private Date period;
    private Date payDate;
    private float maxLossRt;
    private float maxProfitRt;
}
