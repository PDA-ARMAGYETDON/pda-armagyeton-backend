package com.example.stock_system.transferHistory;

import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;

    @Operation(summary = "계좌 이체 내역 조회", description = "모임계좌를 제외한 내 계좌의 이체내역을 조회한다.")
    @PostMapping("accounts/{id}/transactions")
    public AccountTransferDetailDto getAccountTransferDetail(@PathVariable int id) {

        return transferHistoryService.getAccountTransferDetail(id);

    }
}
