package com.avocadogroup.zenith.lock.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class TestResponse {
    private BigDecimal finalBalance;
    private int successCount;
    private int failCount;
}
