package com.example.stock_system.account.utils;

import com.example.common.dto.ApiResponse;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AccountCommunicator {

    private final WebClient.Builder webClientBuilder;

    @Value("${ag.url}")
    private String AG_URL;

    public Category getCategoryFromGroupInvestmentSystem(int id) {
        WebClient webClient = webClientBuilder.build();

        ApiResponse<Category> category = webClient.get()
                .uri(AG_URL + ":8081/api/teams/" + id + "/" + "category")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Category>>() {
                })
                .block();
        if (!category.isSuccess()) {
            throw new AccountException(AccountErrorCode.GROUP_INVESTMENT_SERVER_BAD_REQUEST);
        }
        return category.getData();
    }
}
