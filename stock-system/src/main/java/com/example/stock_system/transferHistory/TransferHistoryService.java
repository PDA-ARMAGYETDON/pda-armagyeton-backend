package com.example.stock_system.transferHistory;

import com.example.stock_system.account.Account;
import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.TeamAccount;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.transferHistory.dto.AccountTransferDetailDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TransferHistoryService {

    private final TransferHistoryRepository transferHistoryRepository;
    private final TeamAccountRepository teamAccountRepository;
    private final AccountRepository accountRepository;


    private final String PRIVATE_ACCOUNT = "81901";
    private final String TEAM_ACCOUNT = "81902";

    @Transactional
    public Page<AccountTransferDetailDto> getPrivateAccountTransferDetail(int userId, int page, int size) {

        Account foundedPrivateAccount = accountRepository
                .findByUserIdAndAccountNumberContaining(userId, PRIVATE_ACCOUNT)
                .orElseThrow(() -> new AccountException(
                        AccountErrorCode.PRIVATE_ACCOUNT_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<AccountTransferDetailDto> foundedTransferDetailList = transferHistoryRepository
                .findByAccountId(foundedPrivateAccount.getId(), pageRequest);

        return foundedTransferDetailList;

    }

    public Page<AccountTransferDetailDto> getTeamAccountTransferDetail(int teamId, int page, int size) {
        TeamAccount foundedTeamAccount = teamAccountRepository.findByTeamId(teamId).orElseThrow(
                () -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<AccountTransferDetailDto> foundedTransferDetailList = transferHistoryRepository
                .findByAccountId(foundedTeamAccount.getId(), pageRequest);

        return foundedTransferDetailList;
    }
}

