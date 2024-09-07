package com.example.invest_references.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class News {
    private String press;
    private String title;
    private String url;
    private String date;

    @Builder
    public News(String press, String title, String url, String date) {
        this.press = press;
        this.title = title;
        this.url = url;
        this.date = date;
    }
}
