package com.example.alarm.firebase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockAlarmDto {

    private int teamId;

    private String teamName;

    private String stockName;

    private int quantity;

    private boolean isBuy;

}