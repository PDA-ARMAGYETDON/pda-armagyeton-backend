package com.example.stock_system.transferHistory;

import com.example.stock_system.account.AccountRepository;
import com.example.stock_system.account.TeamAccountRepository;
import com.example.stock_system.account.exception.AccountErrorCode;
import com.example.stock_system.account.exception.AccountException;
import com.example.stock_system.transferHistory.dto.TransferDetailDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public Page<TransferDetailDto> getPrivateAccountTransferDetail(int userId, int page, int size) {

        int foundedPrivateAccountId = accountRepository
                .findAllByUserIdAndAccountNumberContaining(userId, PRIVATE_ACCOUNT)
                .orElseThrow(() -> new AccountException(
                        AccountErrorCode.PRIVATE_ACCOUNT_NOT_FOUND)).getId();

        Page<TransferDetailDto> pageResult = getTransferDetails(page, size, foundedPrivateAccountId);

        return pageResult;
    }


    public Page<TransferDetailDto> getTeamAccountTransferDetail(int teamId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        int foundedTeamAccountId = teamAccountRepository.findByTeamId(teamId).orElseThrow(
                () -> new AccountException(AccountErrorCode.TEAM_ACCOUNT_NOT_FOUND)).getId();

        Page<TransferDetailDto> pageResult = getTransferDetails(page, size, foundedTeamAccountId);

        return pageResult;

    }


    private Page<TransferDetailDto> getTransferDetails(int page, int size, int Id) {
        List<TransferHistory> foundedTransferToOtherEntity = transferHistoryRepository
                .findAllByAccountId(Id);

        List<TransferDetailDto> foundedTransferToOtherList = foundedTransferToOtherEntity.stream()
                .map(transferHistory -> new TransferDetailDto().fromToOtherEntity(transferHistory))
                .toList();

        List<TransferHistory> foundedTransferToMeEntity = transferHistoryRepository
                .findAllByReceivingAccountId(Id);

        List<TransferDetailDto> foundedTransferToMeList = foundedTransferToMeEntity.stream()
                .map(transferHistory -> new TransferDetailDto().fromToMeEntity(transferHistory))
                .toList();

        PageRequest pageRequest = PageRequest.of(page, size);

        List<TransferDetailDto> mergedList = new ArrayList<>();
        mergedList.addAll(foundedTransferToOtherList);
        mergedList.addAll(foundedTransferToMeList);

        mergedList.sort(Comparator.comparing(TransferDetailDto::getTransferDate).reversed());

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), mergedList.size());

        if (start > end || start < 0 || end > mergedList.size()) {
            throw new IllegalArgumentException("잘못된 페이지 범위입니다.");
        }

        return new PageImpl<>(mergedList.subList(start, end), pageRequest, mergedList.size());
    }
}

