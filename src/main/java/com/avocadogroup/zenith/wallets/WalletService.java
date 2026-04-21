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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

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

    // Lock registry (only one lock per wallet ID)
    private final ConcurrentHashMap<Long, ReentrantLock> walletLocks = new ConcurrentHashMap<>(); // ConcurrentHashMap is thread-safe Map 

    /**
     * Returns a lock for a specific wallet ID.
     *
     * @param walletId The ID of the wallet to get a lock for.
     * @return A lock for the specified wallet ID.
     */
    private ReentrantLock getLockForWallet(Long walletId) {
        return walletLocks.computeIfAbsent(walletId, id -> new ReentrantLock()); // Create a new lock if it doesn't exist
    }

    /**
     * Deposits a positive amount into the specified wallet.
     *
     * @param userId   The ID of the authenticated user
     * @param walletId The ID of the wallet to deposit into
     * @param request  The transaction request containing the deposit amount
     * @return DTO representation of the wallet with the updated balance
     * @throws ResourceNotFoundException If the wallet does not exist or does not belong to the user.
     */
    @Transactional // Database-level protection
    public WalletDto deposit(Long userId, Long walletId, WalletTransactionRequest request) {
        // Get the lock for this wallet
        ReentrantLock lock = getLockForWallet(walletId);

        // Acquire the lock
        lock.lock();

        // Try to deposit
        try {
            // Fetch the wallet with a pessimistic write lock to prevent concurrent modification
            var wallet = walletRepository.findByIdAndUserIdForUpdate(walletId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

            // Artificial delay to simulate processing and expose race conditions
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Add the deposited amount to the current balance
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));

            // Update the balance in the database
            walletRepository.save(wallet);

            // Convert and return the updated wallet as a DTO
            return walletMapper.toDto(wallet);
        } finally {
            // Release the lock
            lock.unlock();
        }
    }

    /**
     * Withdraws a positive amount from the specified wallet.
     *
     * @param userId   The ID of the authenticated user
     * @param walletId The ID of the wallet to withdraw from
     * @param request  The transaction request containing the withdrawal amount
     * @return DTO representation of the wallet with the updated balance
     * @throws ResourceNotFoundException If the wallet does not exist or does not belong to the user.
     * @throws BadRequestException       If the withdrawal amount exceeds the available balance.
     */
    @Transactional // Database-level protection
    public WalletDto withdraw(Long userId, Long walletId, WalletTransactionRequest request) {
        // Get the lock for this wallet
        ReentrantLock lock = getLockForWallet(walletId);

        // Acquire the lock
        lock.lock();

        // Try to withdraw
        try {
            // Fetch the wallet
            var wallet = walletRepository.findByIdAndUserIdForUpdate(walletId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

            // Delay to simulate processing and expose race conditions
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            // Get the balance
            var balance = wallet.getBalance();

            // Check if the wallet has sufficient funds for the withdrawal (no overdraft allowed)
            if (balance.compareTo(request.getAmount()) < 0) {
                // Reject the withdrawal to prevent negative balance
                throw new BadRequestException("Insufficient balance. Available: " + wallet.getBalance());
            }

            // Subtract the withdrawal amount from the current balance
            wallet.setBalance(balance.subtract(request.getAmount()));

            // Update the balance in the database
            walletRepository.save(wallet);

            // Convert and return the updated wallet as a DTO
            return walletMapper.toDto(wallet);
        } finally {
            // Release the lock
            lock.unlock();
        }

    }
}
