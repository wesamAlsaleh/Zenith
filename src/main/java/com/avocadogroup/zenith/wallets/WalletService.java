package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.common.exceptions.BadRequestException;
import com.avocadogroup.zenith.common.exceptions.DuplicateResourceException;
import com.avocadogroup.zenith.common.exceptions.ResourceNotFoundException;
import com.avocadogroup.zenith.users.User;
import com.avocadogroup.zenith.users.UserRepository;
import com.avocadogroup.zenith.wallets.dtos.CreateWalletRequest;
import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import com.avocadogroup.zenith.wallets.dtos.WalletTransactionRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final UserRepository userRepository;

    /**
     * Creates a default BHD wallet during user registration.
     * <p>
     * This method is called internally by the authentication service when a new user
     * registers. It initializes a wallet with BHD currency and a name derived from
     * the user's email prefix.
     * </p>
     *
     * @param user The newly registered user entity.
     * @return DTO representation of the created wallet.
     */
    public WalletDto createWallet(User user) {
        // Initialize a new wallet entity
        Wallet wallet = new Wallet();

        // Link the wallet to the owning user
        wallet.setUser(user);

        // Set the default currency to BHD (Bahraini Dinar)
        wallet.setCurrency(Currency.BHD);

        // Derive wallet name from the user's email prefix (part before @)
        wallet.setWalletName(user.getEmail().substring(0, user.getEmail().indexOf("@")));

        // Persist the wallet record in the database
        walletRepository.save(wallet);

        // Convert and return the saved entity as a DTO
        return walletMapper.toDto(wallet);
    }

    /**
     * Opens a new currency account (wallet) for the authenticated user.
     * <p>
     * Enforces the one-wallet-per-currency business rule at the application level
     * before persisting. The database unique constraint serves as a safety net
     * for any race conditions that bypass this check.
     * </p>
     *
     * @param userId  The ID of the authenticated user.
     * @param request The request containing the desired currency and wallet name.
     * @return DTO representation of the newly created wallet.
     * @throws DuplicateResourceException If the user already has a wallet in the specified currency.
     * @throws ResourceNotFoundException  If the user does not exist in the database.
     */
    @Transactional
    public WalletDto openAccount(Long userId, CreateWalletRequest request) {
        // Check if the user already has a wallet with the requested currency
        if (walletRepository.existsByUserIdAndCurrency(userId, request.getCurrency())) {
            // Throw a duplicate error to prevent creating two wallets with the same currency
            throw new DuplicateResourceException(
                    "You already have a " + request.getCurrency().getDisplayName() + " wallet"
            );
        }

        // Fetch the user entity from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Initialize a new wallet entity
        Wallet wallet = new Wallet();

        // Link the wallet to the owning user
        wallet.setUser(user);

        // Set the currency from the request
        wallet.setCurrency(request.getCurrency());

        // Set the wallet display name from the request
        wallet.setWalletName(request.getWalletName());

        // Save the new wallet record in the db
        walletRepository.save(wallet);

        // Convert and return the saved entity as a DTO
        return walletMapper.toDto(wallet);
    }

    /**
     * Retrieves all wallets belonging to the authenticated user.
     * <p>
     * Returns wallets across all currencies. The list may be empty
     * if no wallets exist (though at minimum one BHD wallet is created on registration).
     * </p>
     *
     * @param userId The ID of the authenticated user.
     * @return A list of wallet DTOs ordered by database default (creation order).
     */
    public List<WalletDto> getWallets(Long userId) {
        // Fetch all wallet records of the user
        List<Wallet> wallets = walletRepository.findAllByUserId(userId);

        // Create a list of walletDto
        List<WalletDto> walletDtos = new ArrayList<>();

        // Convert each wallet to DTo and add it in the list
        wallets.forEach(wallet -> {
            walletDtos.add(walletMapper.toDto(wallet));
        });

        // Return list of wallets DTOs
        return walletDtos;
    }

    /**
     * Retrieves a specific wallet by ID, ensuring it belongs to the authenticated user.
     * <p>
     * The ownership check is performed at the query level, preventing users
     * from accessing wallets that belong to other accounts.
     * </p>
     *
     * @param userId   The ID of the authenticated user.
     * @param walletId The ID of the wallet to retrieve.
     * @return DTO representation of the requested wallet.
     * @throws ResourceNotFoundException If the wallet does not exist or does not belong to the user.
     */
    public WalletDto getWallet(Long userId, Long walletId) {
        // Fetch the wallet by ID and verify ownership via userId
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Convert and return the entity as a DTO
        return walletMapper.toDto(wallet);
    }

    /**
     * Deposits a positive amount into the specified wallet.
     * <p>
     * Uses pessimistic write locking to prevent race conditions during concurrent deposits.
     * The lock is acquired at the database row level and held until the transaction commits.
     * </p>
     *
     * @param userId   The ID of the authenticated user (for ownership verification).
     * @param walletId The ID of the wallet to deposit into.
     * @param request  The transaction request containing the deposit amount.
     * @return DTO representation of the wallet with the updated balance.
     * @throws ResourceNotFoundException If the wallet does not exist or does not belong to the user.
     */
    @Transactional
    public WalletDto deposit(Long userId, Long walletId, WalletTransactionRequest request) {
        // Fetch the wallet with a pessimistic write lock to prevent concurrent modification
        Wallet wallet = walletRepository.findByIdAndUserIdForUpdate(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Add the deposited amount to the current balance
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        // Persist the updated balance to the database
        walletRepository.save(wallet);

        // Convert and return the updated wallet as a DTO
        return walletMapper.toDto(wallet);
    }

    /**
     * Withdraws a positive amount from the specified wallet.
     * <p>
     * Enforces a strict no-overdraft policy: the withdrawal is rejected if the
     * requested amount exceeds the available balance. Uses pessimistic write locking
     * to guarantee the balance check and subtraction are atomic under concurrency.
     * </p>
     *
     * @param userId   The ID of the authenticated user (for ownership verification).
     * @param walletId The ID of the wallet to withdraw from.
     * @param request  The transaction request containing the withdrawal amount.
     * @return DTO representation of the wallet with the updated balance.
     * @throws ResourceNotFoundException If the wallet does not exist or does not belong to the user.
     * @throws BadRequestException       If the withdrawal amount exceeds the available balance.
     */
    @Transactional
    public WalletDto withdraw(Long userId, Long walletId, WalletTransactionRequest request) {
        // Fetch the wallet
        Wallet wallet = walletRepository.findByIdAndUserIdForUpdate(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Get the balance
        var balance = wallet.getBalance();

        // Check if the wallet has sufficient funds for the withdrawal (no overdraft allowed)
        if (balance.compareTo(request.getAmount()) < 0) {
            // Reject the withdrawal to prevent negative balance
            throw new BadRequestException("Insufficient balance. Available: " + wallet.getBalance());
        }

        // Subtract the withdrawal amount from the current balance
        wallet.setBalance(balance.subtract(request.getAmount()));

        // Updated balance to the database
        walletRepository.save(wallet);

        // Convert and return the updated wallet as a DTO
        return walletMapper.toDto(wallet);
    }
}
