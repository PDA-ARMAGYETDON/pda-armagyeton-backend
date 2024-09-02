package com.example.stock_system.transferHistory;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;

    @Operation(summary = "계좌 이체 내역 조회", description = "모임계좌를 제외한 내 계좌의 이체내역을 조회한다.")
    @GetMapping("/api/accounts/{id}/transactions")
    public ApiResponse<List<AccountTransferDetailDto>> getAccountTransferDetail(@PathVariable int id) {

        List<AccountTransferDetailDto> accountTransferDetailList = transferHistoryService.getAccountTransferDetail(id);
        return new ApiResponse<>(200, true, "계좌 이체 내역 조회에 성공하였습니다.", accountTransferDetailList);

    }
}
