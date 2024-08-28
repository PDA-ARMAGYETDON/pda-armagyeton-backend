package com.example.stock_system.transferHistory;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;
}
