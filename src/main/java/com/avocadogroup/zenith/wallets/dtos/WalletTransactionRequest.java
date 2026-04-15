package com.avocadogroup.zenith.wallets.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransactionRequest {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", message = "Amount must be at least 0.1")
    @Digits(integer = 15, fraction = 4, message = "Amount must have at most 15 digits and 4 decimal places")
    private BigDecimal amount;
}
