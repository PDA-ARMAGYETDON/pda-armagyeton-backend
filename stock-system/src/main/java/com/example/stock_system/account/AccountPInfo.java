package com.example.stock_system.account;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class AccountPInfo {
    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    private String pInfo;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public AccountPInfo(Account acccount, String accountPInfo){
        this.account = acccount;
        this.pInfo = accountPInfo;
    }

    public AccountPInfo() {

    }
}
