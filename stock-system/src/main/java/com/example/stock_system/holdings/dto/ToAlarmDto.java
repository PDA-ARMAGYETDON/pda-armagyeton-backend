package com.example.stock_system.holdings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ToAlarmDto {

    private int teamId;

    private String stockName;

    private int quantity;

    private int price;

    private boolean isBuy;

}
