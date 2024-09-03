package com.example.stock_system.transferHistory;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;

    @Operation(summary = "개인계좌 이체 내역 조회", description = "개인 계좌의 이체내역을 조회.\n ex).../history/private/{id}?page=0&size=10")
    @GetMapping("/api/accounts/history/private")
    public ApiResponse<Page<AccountTransferDetailDto>> getPrivateTransferDetail(
            @RequestParam int page, @RequestParam int size) {

        //FIXME: 토큰 값 아닌 임시로 아이디 부여
        int id = 1;

        Page<AccountTransferDetailDto> accountTransferDetailList = transferHistoryService
                .getPrivateAccountTransferDetail(id, page, size);
        return new ApiResponse<>(200, true, "계좌 이체 내역 조회에 성공하였습니다.", accountTransferDetailList);

    }

    @Operation(summary = "모임계좌 이체 내역 조회", description = "모임계좌의 이체내역을 조회.\n ex).../history/team?page=0&size=10")
    @GetMapping("/api/accounts/history/team")
    public ApiResponse<Page<AccountTransferDetailDto>> getTeamTransferDetail(
            @RequestParam int page, @RequestParam int size) {

        //FIXME: 토큰 값 아닌 임시로 아이디 부여
        int id = 101;

        Page<AccountTransferDetailDto> accountTransferDetailList = transferHistoryService
                .getTeamAccountTransferDetail(id, page, size);
        return new ApiResponse<>(200, true, "계좌 이체 내역 조회에 성공하였습니다.", accountTransferDetailList);

    }
}
