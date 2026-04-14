package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.authentication.services.AuthenticationService;
import com.avocadogroup.zenith.wallets.dtos.CreateWalletRequest;
import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import com.avocadogroup.zenith.wallets.dtos.WalletTransactionRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for wallet operations.
 * <p>
 * All endpoints require authentication. The authenticated user's ID is extracted
 * from the security context and passed to the service layer for ownership enforcement.
 * </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;
    private final AuthenticationService authenticationService;

    /**
     * Creates a new wallet account for the authenticated user.
     * <p>
     * The user must specify a currency and wallet name. Duplicate currencies
     * per user are rejected.
     * </p>
     *
     * @param request The validated wallet creation request containing currency and name.
     * @return A 201 Created response with the new wallet DTO.
     */
    @PostMapping
    public ResponseEntity<WalletDto> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Creation a wallet (open account)
        WalletDto wallet = walletService.openAccount(userId, request);

        // Return HTTP 201 Created with the new wallet details
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    /**
     * Retrieves all wallets belonging to the authenticated user.
     * <p>
     * Returns wallets across all currencies. At minimum, the default BHD wallet
     * created during registration will be present.
     * </p>
     *
     * @return A 200 OK response with the list of wallet DTOs.
     */
    @GetMapping
    public ResponseEntity<List<WalletDto>> getWallets() {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Fetch all the wallets for the authenticated user
        List<WalletDto> wallets = walletService.getWallets(userId);

        // Return HTTP 200 OK with the wallet list
        return ResponseEntity.ok(wallets);
    }

    /**
     * Retrieves a specific wallet by its ID for the authenticated user.
     * <p>
     * Returns 404 if the wallet does not exist or does not belong to the requester.
     * </p>
     *
     * @param walletId The ID of the wallet to retrieve.
     * @return A 200 OK response with the wallet DTO.
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable Long walletId) {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Fetch the specific wallet of the authenticated user
        WalletDto wallet = walletService.getWallet(userId, walletId);

        // Return HTTP 200 OK with the wallet details
        return ResponseEntity.ok(wallet);
    }

    /**
     * Deposits money into a specific wallet owned by the authenticated user.
     * <p>
     * The amount must be positive (minimum 0.01). The operation is atomic
     * and safe under concurrent access.
     * </p>
     *
     * @param walletId The ID of the wallet to deposit into.
     * @param request  The validated transaction request containing the deposit amount.
     * @return A 200 OK response with the updated wallet DTO showing the new balance.
     */
    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<WalletDto> deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody WalletTransactionRequest request
    ) {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Deposit to the account
        WalletDto wallet = walletService.deposit(userId, walletId, request);

        // Return HTTP 200 OK with the updated wallet balance
        return ResponseEntity.ok(wallet);
    }

    /**
     * Withdraws money from a specific wallet owned by the authenticated user.
     * <p>
     * The amount must be positive (minimum 0.01) and must not exceed the
     * available balance (no overdraft allowed). The operation is atomic
     * and safe under concurrent access.
     * </p>
     *
     * @param walletId The ID of the wallet to withdraw from.
     * @param request  The validated transaction request containing the withdrawal amount.
     * @return A 200 OK response with the updated wallet DTO showing the new balance.
     */
    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<WalletDto> withdraw(
            @PathVariable Long walletId,
            @Valid @RequestBody WalletTransactionRequest request
    ) {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Withdrawal from the account
        WalletDto wallet = walletService.withdraw(userId, walletId, request);

        // Return HTTP 200 OK with the updated wallet balance
        return ResponseEntity.ok(wallet);
    }
}
