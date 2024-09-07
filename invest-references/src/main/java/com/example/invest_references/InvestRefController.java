package com.example.invest_references;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class InvestRefController {
    private final InvestRefService investRefService;

}
