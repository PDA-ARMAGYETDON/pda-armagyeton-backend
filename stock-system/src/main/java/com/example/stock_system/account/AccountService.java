package com.example.stock_system.account;

import com.example.stock_system.account.dto.AccountDto;
import com.example.stock_system.account.dto.AccountDtoConverter;
import com.example.stock_system.account.dto.CreateAccountRequest;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountPInfoRepository accountPInfoRepository;
    private final AccountDtoConverter accountDtoConverter;
    public AccountDto getAccount(int id) {
        Account account = accountRepository.findById(id).orElseThrow(
                ()->new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        return accountDtoConverter.fromEntity(account);
    }

    public Account createTeamAccount(){

        String accountNumber = "81902" + generateRandomNumber();
        Account account = new Account(accountNumber);

        return accountRepository.save(account);
    }

    public Account createPersonalAccount(){

        String accountNumber = "81901"+ generateRandomNumber();
        Account account = new Account(accountNumber);

        return accountRepository.save(account);
    }

    public void createAccountPInfo(Account account, CreateAccountRequest accountPInfo){
        AccountPInfo createdAccountPInfo = new AccountPInfo(account,accountPInfo.getAccountPInfo());
        accountPInfoRepository.save(createdAccountPInfo);
    }

    private static String generateRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

}
