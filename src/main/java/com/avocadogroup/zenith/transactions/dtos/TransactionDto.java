package com.avocadogroup.zenith.transactions.dtos;

import com.avocadogroup.zenith.transactions.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private String senderWalletName;
    private String receiverWalletName;
    private BigDecimal amount;
    private TransactionType type;
    private Instant createdAt;
}
