package com.example.armagyetdon.account;

import com.example.armagyetdon.transferHistory.TransferHistory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToOne(mappedBy = "account")
    private AccountPInfo accountPInfo;


    @OneToMany(mappedBy = "author")
    private List<TransferHistory> transferHistories;

    // TODO: receiving account id와의 연관관계를 넣어줄 것인가?
}
