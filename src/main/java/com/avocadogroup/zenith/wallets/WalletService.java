package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.users.User;
import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    /**
     * Initializes and persists a default BHD wallet for a new user.
     *
     * @param user The owner of the new wallet.
     * @return DTO representation of the created wallet.
     */
    public WalletDto createWallet(User user) {
        // Create wallet entity
        Wallet wallet = new Wallet();

        // Link the user to the wallet
        wallet.setUser(user);

        // Set the default fields
        wallet.setCurrency(Currency.BHD.name()); // .name() to use the "BHD"
        wallet.setWalletName(user.getEmail().substring(0, user.getEmail().indexOf("@"))); // Set the default name to the user email

        // Save the wallet record in the db
        walletRepository.save(wallet);

        // Return the created wallet as DTO
        return walletMapper.toDto(wallet);
    }
}
