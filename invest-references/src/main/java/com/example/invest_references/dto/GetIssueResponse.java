package com.example.invest_references.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetIssueResponse {
    List<ShinhanData> risingRanking;
    List<ShinhanData> hotVolume;
    List<ShinhanData> hotEarning;
}
