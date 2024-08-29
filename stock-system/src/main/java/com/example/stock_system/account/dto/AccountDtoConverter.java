package com.example.stock_system.account.dto;

import com.example.stock_system.account.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoConverter {
    public AccountDto fromEntity(Account account){
        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .deposit(account.getDeposit())
                .totalEvluAmt(account.getTotalEvluAmt())
                .totalPchsAmt(account.getTotalPchsAmt())
                .totalEvluPfls(account.getTotalEvluPfls())
                .totalEvluPflsRt(account.getTotalEvluPflsRt())
                .build();
    }

}
