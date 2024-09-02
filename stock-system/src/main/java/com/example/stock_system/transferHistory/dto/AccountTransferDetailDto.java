package com.example.stock_system.transferHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AccountTransferDetailDto {

    private int account;

    private LocalDateTime transferAt;

    private int transferAmt;

    private int receivingAccount;

}
