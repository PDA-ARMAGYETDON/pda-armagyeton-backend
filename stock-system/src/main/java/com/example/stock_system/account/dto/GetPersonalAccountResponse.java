package com.example.stock_system.account.dto;

import com.example.stock_system.account.Account;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPersonalAccountResponse {
    private String accountNumber;
    private int totalAsset;

    public GetPersonalAccountResponse() {
    }

    @Builder
    public GetPersonalAccountResponse(String accountNumber, int totalAsset) {
        this.accountNumber = accountNumber;
        this.totalAsset = totalAsset;
    }

    public GetPersonalAccountResponse fromEntity(Account account) {
        return GetPersonalAccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .totalAsset(account.getDeposit() + account.getTotalEvluAmt())
                .build();
    }
}
