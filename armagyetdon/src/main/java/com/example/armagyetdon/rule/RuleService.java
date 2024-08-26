package com.example.armagyetdon.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RuleService {
    private final RuleRepository ruleRepository;
}
