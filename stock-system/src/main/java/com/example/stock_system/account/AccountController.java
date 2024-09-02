package com.example.stock_system.account;

import com.example.stock_system.account.dto.AccountDto;
import com.example.common.dto.ApiResponse;
import com.example.stock_system.account.dto.CreateAccountRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public ApiResponse<AccountDto> getAccount(@PathVariable("id") int id) {
        AccountDto accountDto = accountService.getAccount(id);
        return new ApiResponse<>(200, true, "계좌를 조회했습니다.", accountDto);
    }

    @PostMapping("/personal")
    public ApiResponse<AccountDto> createPersonalAccount(@RequestBody CreateAccountRequest createAccountRequest){
        Account savedAccount = accountService.createPersonalAccount();
        accountService.createAccountPInfo(savedAccount,createAccountRequest);
        return new ApiResponse<>(201, true, "계좌가 생성되었습니다.", null);
    }

    @PostMapping("/team")
    public ApiResponse<AccountDto> createTeamAccount(@RequestBody CreateAccountRequest createAccountRequest){
        Account savedAccount = accountService.createTeamAccount();
        accountService.createAccountPInfo(savedAccount,createAccountRequest);
        return new ApiResponse<>(201, true, "모임 계좌가 생성되었습니다.", null);
    }

}
