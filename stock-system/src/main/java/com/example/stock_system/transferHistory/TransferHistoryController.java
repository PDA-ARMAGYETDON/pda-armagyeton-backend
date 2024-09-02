package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;

    @PostMapping("accounts/{id}/transactions")
    public AccountTransferDetailDto getAccountTransferDetail(@PathVariable int id) {

        return transferHistoryService.getAccountTransferDetail(id);

    }
}
