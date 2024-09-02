package com.example.stock_system.holdings;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.holdings.dto.HoldingsDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/holdings")
public class HoldingsController {
    private final HoldingsService holdingsService;

    @GetMapping("/{id}")
    public ApiResponse<List<HoldingsDto>> getHoldings(@PathVariable int id) {
        List<Holdings> holdingsList = holdingsService.getHoldings(id);
        List<HoldingsDto> holdingsDtoList = holdingsList.stream()
                .map(HoldingsDto::new)
                .toList();
        return new ApiResponse<>(200, true, "보유종목을 조회했습니다.", holdingsDtoList);
    }


}
