package com.example.stock_system.holdings.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class ClosingPirceRequest {

    private Map<String, String> output;

    private String rt_cd;
    private String msg_cd;
    private String msg1;

    public ClosingPirceRequest(Map<String, String> output, String rt_cd, String msg_cd, String msg1) {
        this.output = output;
        this.rt_cd = rt_cd;
        this.msg_cd = msg_cd;
        this.msg1 = msg1;
    }

    public ClosingPirceRequest(){

    }


}
