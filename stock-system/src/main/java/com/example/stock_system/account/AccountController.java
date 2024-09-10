package com.example.stock_system.account;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.account.dto.*;
import com.example.stock_system.holdings.HoldingsService;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.ranking.RankingService;
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
    private final RankingService rankingService;


    @Operation(summary = "개인 계좌 생성", description = "비밀번호를 입력값으로 받아 개인 계좌를 생성해줍니다.")
    @PostMapping("/personal")
    public ApiResponse<AccountDto> createPersonalAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        Account savedAccount = accountService.createPersonalAccount(createAccountRequest.getName(), createAccountRequest.getUserId());
        accountService.createAccountPInfo(savedAccount, createAccountRequest);
        return new ApiResponse<>(201, true, "계좌가 생성되었습니다.", null);
    }

    @Operation(summary = "팀 계좌 생성", description = "비밀번호를 입력값으로 받아 팀 계좌를 생성해줍니다.")
    @PostMapping("/team")
    public ApiResponse<AccountDto> createTeamAccount(@RequestBody CreateAccountRequest createAccountRequest, @RequestAttribute("teamId") int teamId) {
        Account savedAccount = accountService.createTeamAccount(createAccountRequest.getName(), createAccountRequest.getUserId(), teamId);
        accountService.createAccountPInfo(savedAccount, createAccountRequest);
        rankingService.registRanking(savedAccount);
        return new ApiResponse<>(201, true, "모임 계좌가 생성되었습니다.", null);
    }


    @Operation(summary = "실시간 계좌 보유 종목들의 각 data 조회", description = "실시간 데이터를 불러오는 API")
    @GetMapping(value = "/realtime/{teamId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<HoldingsDto> getRealTimeHoldingsByTeamId(@PathVariable int teamId) {
        return holdingsService.getRealTimeHoldingsByTeamId(teamId);
    }

    @Operation(summary = "실시간 계좌 보유 종목 합 data 조회", description = "실시간 데이터를 불러오는 API")
    @GetMapping(value = "/sum-realtime/{teamId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GetTeamAccountResponse> streamRealTimeSumByTeamId(@PathVariable int teamId) {
        return accountService.getRealTimeSumByTeamId(teamId);
    }

    @Operation(summary = "개인계좌 조회", description = "개인 계좌 조회")
    @GetMapping("/personal")
    public ApiResponse<GetPersonalAccountResponse> getPersonalAccount(@RequestAttribute("userId") int userId) {
        return new ApiResponse<>(200, true, "개인 계좌 조회 성공", new GetPersonalAccountResponse().fromEntity(accountService.getPersonalAccount(userId)));
    }

    @Operation(summary = "모임계좌 조회", description = "모임 계좌 조회")
    @GetMapping("/team")
    public ApiResponse<GetTeamAccountResponse> getTeamAccount(@RequestAttribute("teamId") int teamId) {
        return new ApiResponse<>(200, true, "모임 계좌 조회 성공", new GetTeamAccountResponse().fromEntity(accountService.getTeamAccount(teamId)));
    }

}
