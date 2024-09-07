package com.example.invest_references;

import com.example.common.dto.ApiResponse;
import com.example.invest_references.dto.News;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class InvestRefController {
    private final InvestRefService investRefService;

    @Operation(summary = "주요 뉴스 조회", description = "네이버 증권에서 주요 뉴스 5개를 가져옵니다.")
    @GetMapping("/api/news")
    public ApiResponse<List<News>> getNews() {
        return new ApiResponse<>(200, true, "주요 뉴스를 가져왔습니다.", investRefService.getNews());
    }
}
