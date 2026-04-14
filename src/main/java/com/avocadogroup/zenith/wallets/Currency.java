package com.avocadogroup.zenith.wallets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    BHD("Bahraini Dinar", "BD"),
    GBP("British Pound", "£");

    private final String displayName;
    private final String symbol;
}
