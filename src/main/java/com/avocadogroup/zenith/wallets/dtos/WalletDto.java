package com.avocadogroup.zenith.wallets.dtos;

import com.avocadogroup.zenith.wallets.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;


@AllArgsConstructor
@Data
public class WalletDto {
    private Long id;
    private String walletName;
    private Currency currency;
    private BigDecimal balance;
    private Instant createdAt;
    private Instant updatedAt;
}
