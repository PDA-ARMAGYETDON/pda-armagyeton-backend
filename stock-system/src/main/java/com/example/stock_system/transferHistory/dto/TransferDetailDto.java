package com.example.stock_system.transferHistory.dto;

import com.example.stock_system.transferHistory.TransferHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TransferDetailDto {
    private String name;
    private LocalDateTime transferDate;
    private int transferAmt;

    @Builder
    public TransferDetailDto(String name, LocalDateTime transferDate, int transferAmt) {
        this.name = name;
        this.transferDate = transferDate;
        this.transferAmt = transferAmt;
    }

    //입금
    public TransferDetailDto fromToMeEntity(TransferHistory transferHistory) {
        return TransferDetailDto.builder()
                .name(transferHistory.getSenderName())
                .transferDate(transferHistory.getTransferAt())
                .transferAmt(transferHistory.getTransferAmt())
                .build();
    }

    //송금
    public TransferDetailDto fromToOtherEntity(TransferHistory transferHistory) {
        return TransferDetailDto.builder()
                .name(transferHistory.getReceiverName())
                .transferDate(transferHistory.getTransferAt())
                .transferAmt(-transferHistory.getTransferAmt())
                .build();
    }

}


