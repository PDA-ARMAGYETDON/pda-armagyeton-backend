package com.example.stock_system.transferHistory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferHistoryService {
    private final TransferHistoryRepository transferHistoryRepository;
}
