package com.example.armagyetdon.account;

import com.example.armagyetdon.transferHistory.TransferHistory;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Account {
    @Id
    @OneToOne
    @JoinColumn(name = "account_p_info_id")
    private AccountPInfo accountPInfo;

    private String accountNumber;
    private int deposit;
    private int totalEvluAmt;
    private int totalPchsAmt;
    private int totalEvluPfls;
    private float totalEvluPflsRt;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "account")
    private List<TransferHistory> transferHistories;

    // TODO: receiving account id와의 연관관계를 넣어줄 것인가?
}
