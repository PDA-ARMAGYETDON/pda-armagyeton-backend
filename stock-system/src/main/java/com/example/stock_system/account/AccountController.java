package com.example.stock_system.account;

import com.example.stock_system.account.dto.*;
import com.example.common.dto.ApiResponse;
import com.example.stock_system.holdings.HoldingsService;
import com.example.stock_system.holdings.dto.HoldingsDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final HoldingsService holdingsService;


    @Operation(summary = "계좌 조회",description = "Account의 Id를 통해 계좌를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<AccountDto> getAccount(@PathVariable("id") int id) {
        AccountDto accountDto = accountService.getAccount(id);
        return new ApiResponse<>(200, true, "계좌를 조회했습니다.", accountDto);
    }

    @Operation(summary = "개인 계좌 생성",description = "비밀번호를 입력값으로 받아 개인 계좌를 생성해줍니다.")
    @PostMapping("/personal")
    public ApiResponse<AccountDto> createPersonalAccount(@RequestBody CreateAccountRequest createAccountRequest){
        Account savedAccount = accountService.createPersonalAccount(createAccountRequest.getName(),createAccountRequest.getUserId());
        accountService.createAccountPInfo(savedAccount,createAccountRequest);
        return new ApiResponse<>(201, true, "계좌가 생성되었습니다.", null);
    }

    @Operation(summary = "팀 계좌 생성",description = "비밀번호를 입력값으로 받아 팀 계좌를 생성해줍니다.")
    @PostMapping("/team")
    public ApiResponse<AccountDto> createTeamAccount(@RequestBody CreateAccountRequest createAccountRequest,@RequestAttribute("teamId") int teamId){
        Account savedAccount = accountService.createTeamAccount(createAccountRequest.getName(),createAccountRequest.getUserId(),teamId);
        accountService.createAccountPInfo(savedAccount,createAccountRequest);
        return new ApiResponse<>(201, true, "모임 계좌가 생성되었습니다.", null);
    }

    @Operation(summary = "자동 이체 서비스",description = "test를 위해 호출식으로 작성, 추후에 배치로 처리")
    @PostMapping("/auto")
    public void autoPaymentAndExpel() {

        List<AccountPayment> accountPayments= accountService.convertPaymentData();
        List<PayFail> payFails = accountService.autoPaymentService(accountPayments);
        accountService.expelMember(payFails);

    }

    @Operation(summary = "실시간 계좌 보유 종목들의 각 data 조회",description = "실시간 데이터를 불러오는 API")
    @GetMapping(value = "/realtime/{teamId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<HoldingsDto> getRealTimeHoldingsByTeamId(@PathVariable int teamId) {
        return holdingsService.getRealTimeHoldingsByTeamId(teamId);
    }

    @Operation(summary = "실시간 계좌 보유 종목 합 data 조회", description = "실시간 데이터를 불러오는 API")
    @GetMapping(value = "/realtime-sum/{teamId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GetTeamAccountResponse> streamRealTimeSumByTeamId(@PathVariable int teamId) {
        return accountService.getRealTimeSumByTeamId(teamId);
    }

    @Operation(summary = "자동 전량 매도 서비스",description = "test를 위해 호출식으로 작성, 추후에 배치로 처리")
    @GetMapping("/all-stock-sell/{teamId}")
    public ApiResponse<Integer> allStockSell(@PathVariable int teamId) {
        int sellMoeny = accountService.allStockSell(teamId);
        return new ApiResponse<>(200,true,"해당 계좌의 주식이 전량 매도 되었습니다.",sellMoeny);
    }
    
    @Operation(summary = "랭킹 생성", description = "test 위해 작성, 추후 배치 처리")
    @GetMapping("/create/ranking")
    public ApiResponse createRanking() {
        accountService.createRanking();
        return new ApiResponse<>(201, true, "랭킹 생성합니다.", null);
    }


}
