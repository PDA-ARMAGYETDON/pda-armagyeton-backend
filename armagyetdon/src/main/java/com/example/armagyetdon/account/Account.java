package com.example.armagyetdon.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private float totalEvluPflsRt;
    private LocalDateTime createdAt;
}
