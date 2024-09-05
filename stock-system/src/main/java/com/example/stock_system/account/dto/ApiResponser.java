package com.example.stock_system.account.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ApiResponser {
    private int code;
    private String message;
    private List<AccountPayment> data;
    private boolean success;
}
