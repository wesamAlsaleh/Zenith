package com.avocadogroup.zenith.wallets.dtos;

import com.avocadogroup.zenith.users.dtos.UserDto;
import com.avocadogroup.zenith.wallets.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public class WalletDto {
    private UserDto user;
    private Currency currency;
    private double balance;
    private Instant createdAt;
}
