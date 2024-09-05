package com.example.stock_system.account.dto;

import com.example.stock_system.account.AccountPInfo;
import lombok.Getter;

@Getter
public class CreateAccountRequest {

    private String name;
    private String accountPInfo;
    private int userId;

    public AccountPInfo toEntity(){
        return AccountPInfo.builder()
                .accountPInfo(accountPInfo)
                .build();
    }

}
