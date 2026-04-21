package com.avocadogroup.zenith.lock.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class TestRequest {
    private Long walletId;
    private Long userId;
    private int threads;
    private BigDecimal amount;
    private String operation; // Deposit || Withdraw
}
