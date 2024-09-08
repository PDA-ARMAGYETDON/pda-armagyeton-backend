package com.example.stock_system.transferHistory;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.transferHistory.dto.TransferDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/history")  // 클래스 레벨에 베이스 URL을 설정
@RequiredArgsConstructor
public class TransferHistoryController {
    private final TransferHistoryService transferHistoryService;

    @Operation(summary = "개인계좌 이체 내역 조회", description = "개인 계좌의 이체내역을 조회.\n ex).../history/private/{id}?page=0&size=10")
    @GetMapping("/private")
    public ApiResponse<Page<TransferDetailDto>> getPrivateTransferDetail(
            @RequestAttribute("userId") int userId,
            @RequestParam int page,
            @RequestParam int size) {
        Page<TransferDetailDto> accountTransferDetailList = transferHistoryService
                .getPrivateAccountTransferDetail(userId, page, size);
        return new ApiResponse<>(200, true, "계좌 이체 내역 조회에 성공하였습니다.", accountTransferDetailList);

    }

    @Operation(summary = "모임계좌 이체 내역 조회", description = "모임계좌의 이체내역을 조회.\n ex).../history/team?page=0&size=10")
    @GetMapping("/team")
    public ApiResponse<Page<TransferDetailDto>> getTeamTransferDetail(
            @RequestAttribute("teamId") int teamId,
            @RequestParam int page,
            @RequestParam int size) {
        Page<TransferDetailDto> accountTransferDetailList = transferHistoryService
                .getTeamAccountTransferDetail(teamId, page, size);
        return new ApiResponse<>(200, true, "계좌 이체 내역 조회에 성공하였습니다.", accountTransferDetailList);

    }
}
