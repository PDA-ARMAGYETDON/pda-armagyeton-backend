package com.example.stock_system.account;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.account.dto.*;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.Category;
import com.example.stock_system.holdings.Holdings;
import com.example.stock_system.holdings.HoldingsRepository;
import com.example.stock_system.holdings.dto.HoldingsDto;
import com.example.stock_system.ranking.Ranking;
import com.example.stock_system.ranking.RankingRepository;
import com.example.stock_system.ranking.dto.RankingDto;
import com.example.stock_system.realTimeStock.RealTimeStockService;
import com.example.stock_system.stocks.StocksService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    @Value("${ag.url}")
    private String AG_URL;

    private final AccountRepository accountRepository;
    private final AccountPInfoRepository accountPInfoRepository;
    private final AccountDtoConverter accountDtoConverter;
    private final RestTemplate restTemplate;
    private final TeamAccountRepository teamAccountRepository;
    private final RealTimeStockService realTimeStockService;
    private final HoldingsRepository holdingsRepository;
    private final StocksService stocksService;
    private final RankingRepository rankingRepository;

    public AccountDto getAccount(int id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        return accountDtoConverter.fromEntity(account);
    }

    public Account createTeamAccount(String name, int userId,int teamId) {

        String accountNumber = "81902" + generateRandomNumber();
        Account account = new Account(name, userId, accountNumber);
        Account savedAccount = accountRepository.save(account);

        TeamAccount teamAccount = new TeamAccount(savedAccount,teamId);
        teamAccountRepository.save(teamAccount);

        processFirstPayment(teamId);

        return savedAccount;
    }

    public Account createPersonalAccount(String name, int userId) {

        String accountNumber = "81901" + generateRandomNumber();
        Account account = new Account(name, userId, accountNumber);
        account.sellStock(25000000);

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

        String url = AG_URL+"/api/group/backend/auto-payment";

        HttpHeaders httpHeaders = new HttpHeaders();

        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponser> response = restTemplate.exchange(url, HttpMethod.POST, entity, ApiResponser.class);

        ApiResponser responseWrapper = response.getBody();

        return responseWrapper.getData();
    }

    public List<PayFail> autoPaymentService(List<AccountPayment> accountPayments) {

        List<PayFail> failedPayments = new ArrayList<>();

        for (AccountPayment accountPayment : accountPayments) {
            List<Integer> failedUsers = new ArrayList<>();

            for (int userId : accountPayment.getUsers()) {
                Account personalAccount = getPersonalAccount(userId);

                if (personalAccount.getDeposit() >= accountPayment.getPaymentMoney()) {
                    personalAccount.buyStock(accountPayment.getPaymentMoney());
                    accountRepository.save(personalAccount);
                    TeamAccount teamAccount = teamAccountRepository.findByTeamId(accountPayment.getTeamId()).orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
                    teamAccount.getAccount().sellStock(accountPayment.getPaymentMoney());
                    accountRepository.save(teamAccount.getAccount());
                } else {
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
        List<Account> accounts = accountRepository.findByUserId(id).orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        return accounts.stream()
                .filter(account -> account.getAccountNumber().startsWith("81901"))
                .findFirst()
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));


    }

    public void expelMember(List<PayFail> payFails) {
        String url = AG_URL+"/api/group/backend/expel-member";


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


    public Flux<GetTeamAccountResponse> getRealTimeSumByTeamId(int teamId) {
        return Mono.fromCallable(() -> teamAccountRepository.findByTeamId(teamId)
                        .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND))
                        .getAccount())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(account ->
                        Mono.fromCallable(() -> holdingsRepository.findByAccount(account))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMapMany(holdingsList -> {
                                    if (holdingsList.isEmpty()) {
                                        return Flux.just(GetTeamAccountResponse.builder()
                                                .accountNumber(account.getAccountNumber())
                                                .totalPchsAmt(0)
                                                .totalEvluAmt(0)
                                                .totalEvluPfls(0)
                                                .totalEvluPflsRt(0.0)
                                                .deposit(account.getDeposit())
                                                .totalAsset(account.getDeposit())
                                                .build());
                                    }

                                    List<String> stockCodes = holdingsList.stream()
                                            .map(holding -> holding.getStockCode().getCode())
                                            .collect(Collectors.toList());

                                    List<HoldingsDto> holdingsDtoList = holdingsList.stream()
                                            .map(HoldingsDto::new)
                                            .collect(Collectors.toList());

                                    return realTimeStockService.getRealTimeHoldingsWithTotalSum(account, holdingsDtoList, stockCodes, teamId);
                                })
                );
    }


    public int allStockSell(int teamId) {
        Account account = teamAccountRepository.findByTeamId(teamId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND))
                .getAccount();

        List<Holdings> holdings = holdingsRepository.findByAccount(account);
        int sumPrice = 0;

        for (Holdings holding : holdings) {
            int price = stocksService.getCurrentData(holding.getStockCode().getCode()).getCurrentPrice();
            price *= holding.getHldgQty();
            sumPrice += price;
            // 주식 매도 후 삭제 (추후 활성화 할 예정)
            // holdingsRepository.delete(holding);
        }

        String url = AG_URL+"/api/group/backend/member";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Integer> entity = new HttpEntity<>(teamId, headers);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, entity, ApiResponse.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Integer> memberUserIds = objectMapper.convertValue(response.getBody().getData(), new TypeReference<List<Integer>>() {});

        int memberCount = memberUserIds.size();

        if (memberCount > 0) {
            int amountPerMember = sumPrice / memberCount;

            for (Integer userId : memberUserIds) {
                Account personalAccount = getPersonalAccount(userId);
                personalAccount.sellStock(amountPerMember);  // 개인 계좌에 입금
                accountRepository.save(personalAccount);  // 변경 사항 저장
            }
        }

        return sumPrice;  // 총 판매 금액 반환
    }



    public void stopRealTimeStream(int teamId) {
        realTimeStockService.stopStreamingByTeamId(teamId);
    }

    public FirstPayment getFirstPaymentFromAPI(int teamId) {
        String url = "http://localhost:8081/api/backend/first-payment?teamId=" + teamId;

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                ApiResponse.class
        );


        ObjectMapper objectMapper = new ObjectMapper();
        FirstPayment firstPayment = objectMapper.convertValue(response.getBody().getData(), FirstPayment.class);

        return firstPayment;
    }

    @Transactional
    public void processFirstPayment(int teamId) {


        FirstPayment firstPayment = getFirstPaymentFromAPI(teamId);


        TeamAccount teamAccount = teamAccountRepository.findByTeamId(firstPayment.getTeamId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        Account teamAccountEntity = teamAccount.getAccount();

        int paymentAmount = firstPayment.getPaymentMoney();

        List<Integer> userIds = firstPayment.getUsers();

        for (Integer userId : userIds) {
            Account personalAccount = getPersonalAccount(userId);

            if (personalAccount.getDeposit() >= paymentAmount) {
                personalAccount.buyStock(paymentAmount);
                accountRepository.save(personalAccount);

                teamAccountEntity.sellStock(paymentAmount);
                accountRepository.save(teamAccountEntity);
            } else {
                throw new AccountException(AccountErrorCode.NOT_ENOUGH_DEPOSIT);
            }
        }
    }

    public void createRanking() {

        List<Account> accounts = accountRepository.findByAccountNumberStartingWith("81902").orElseThrow(()->new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND));

        List<RankingDto> rankingDtos = accounts.stream()
                .map(account -> RankingDto.builder()
                        .account(account)
                        .teamId(teamAccountRepository.findByAccount(account)
                                .orElseThrow(() -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND)).getTeamId())
                        .seedMoney(0)
                        .evluPflsRt(0)
                        .build())
                .collect(Collectors.toList());

        List<Ranking> rankings = rankingDtos.stream()
                .map(RankingDto::toEntity)
                .collect(Collectors.toList());

        rankingRepository.saveAll(rankings);
    }
}
