package com.example.group_investment.rule;

import com.example.group_investment.rule.dto.RuleDto;
import com.example.common.dto.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/rules")

public class RuleController {
    private final RuleService ruleService;

    // RuleResponseDto를 만들어서 response해주세요.
    // ApiResponse를 사용해서 response 형식을 통일해주세요.
    @GetMapping("/{id}")
    public ApiResponse<RuleDto> getRule(@PathVariable int id) {
        return new ApiResponse<>(200, true, "규칙을 조회했습니다.", null);
    }
}
