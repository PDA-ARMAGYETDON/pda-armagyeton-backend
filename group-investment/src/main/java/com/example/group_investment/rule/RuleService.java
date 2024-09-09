package com.example.group_investment.rule;

import com.example.group_investment.rule.dto.CheckDisband;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RuleService {

    private RuleRepository ruleRepository;

    public List<CheckDisband> getCheckDisband() {
        List<Rule> rules = ruleRepository.findAll();
        return rules.stream()
                .map(rule -> new CheckDisband(rule.getTeam().getId(),rule.getMaxLossRt(), rule.getMaxProfitRt()))
                .collect(Collectors.toList());
    }


}
