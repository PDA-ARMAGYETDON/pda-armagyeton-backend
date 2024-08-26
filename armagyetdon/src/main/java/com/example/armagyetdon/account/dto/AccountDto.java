package com.example.armagyetdon.account.dto;

import java.time.LocalDateTime;

public class AccountDto {
    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private float totalEvluPflsRt;
    private LocalDateTime createdAt = LocalDateTime.now();
}
