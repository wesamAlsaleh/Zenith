package com.avocadogroup.zenith.wallets.dtos;

import com.avocadogroup.zenith.wallets.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateWalletRequest {
    @NotNull(message = "Currency is required")
    private Currency currency; // The currency for the new wallet account (must be a valid Currency enum value)

    @NotBlank(message = "Wallet name is required")
    @Size(max = 100, message = "Wallet name must not exceed 100 characters")
    private String walletName;
}
