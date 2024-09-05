package com.example.stock_system.account;

import com.example.stock_system.account.dto.*;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountPInfoRepository accountPInfoRepository;
    private final AccountDtoConverter accountDtoConverter;
    private final RestTemplate restTemplate;
    private final TeamAccountRepository teamAccountRepository;

    public AccountDto getAccount(int id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        return accountDtoConverter.fromEntity(account);
    }

    public Account createTeamAccount(String name, int userId) {

        String accountNumber = "81902" + generateRandomNumber();
        Account account = new Account(name, userId, accountNumber);

        return accountRepository.save(account);
    }

    public Account createPersonalAccount(String name, int userId) {

        String accountNumber = "81901" + generateRandomNumber();
        Account account = new Account(name, userId, accountNumber);

        return accountRepository.save(account);
    }

    public void createAccountPInfo(Account account, CreateAccountRequest accountPInfo) {
        AccountPInfo createdAccountPInfo = new AccountPInfo(account, accountPInfo.getAccountPInfo());
        accountPInfoRepository.save(createdAccountPInfo);
    }

    private static String generateRandomNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }


    public List<AccountPayment> convertPaymentData() {
        String url = "http://localhost:8081/api/teams/autoPayment";

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponser> response = restTemplate.exchange(url, HttpMethod.POST, entity, ApiResponser.class);

        System.out.println(response.getBody());

        ApiResponser responseWrapper = response.getBody();

        return responseWrapper.getData();
    }

    public List<PayFail> autoPaymentService(List<AccountPayment> accountPayments) {

        List<PayFail> failedPayments = new ArrayList<>();

        for(AccountPayment accountPayment: accountPayments){
            List<Integer> failedUsers = new ArrayList<>();

            for(int userId : accountPayment.getUsers()){
                Account personalAccount = getPersonalAccount(userId);

                if(personalAccount.getDeposit()>=accountPayment.getPaymentMoney()){
                    personalAccount.buyStock(accountPayment.getPaymentMoney());
                    accountRepository.save(personalAccount);
                    Optional<TeamAccount> teamAccount = teamAccountRepository.findByTeamId(accountPayment.getTeamId());
                    if (teamAccount.isPresent()) {
                        TeamAccount account = teamAccount.get();
                        account.getAccount().sellStock(accountPayment.getPaymentMoney());
                        accountRepository.save(account.getAccount());
                    }
                }
                else{
                    failedUsers.add(userId);
                }
            }
            if (failedUsers.isEmpty()) {
                failedPayments.add(new PayFail(accountPayment.getTeamId(), null));
            } else {
                failedPayments.add(new PayFail(accountPayment.getTeamId(), failedUsers));
            }

        }
        return failedPayments;

    }

    public Account getPersonalAccount(int id) {
        Optional<List<Account>> accounts = accountRepository.findByUserId(id);

        if (accounts.isPresent()) {
            return accounts.get().stream()
                    .filter(account -> account.getAccountNumber().startsWith("81901"))
                    .findFirst()  // 첫 번째 매칭되는 계좌 찾기
                    .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        }
        throw new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND);
    }

    public void expelMember(List<PayFail> payFails) {
        String url = "http://localhost:8081/api/teams/expelMember";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<PayFail>> entity = new HttpEntity<>(payFails, httpHeaders);

        ResponseEntity<ApiResponser> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ApiResponser.class
        );
    }
}
