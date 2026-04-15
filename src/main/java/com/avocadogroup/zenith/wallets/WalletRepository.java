package com.avocadogroup.zenith.wallets;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Retrieves all wallets belonging to a specific user.
     *
     * @return A list of {@link Wallet} entities (may be empty if no wallets exist).
     */
    List<Wallet> findAllByUserId(Long userId);

    /**
     * Retrieves a specific wallet by its ID, scoped to a particular user.
     */
    Optional<Wallet> findByIdAndUserId(Long id, Long userId);

    /**
     * Checks whether a wallet with the given currency already exists for the user.
     *
     * @return {@code true} if a wallet with this currency already exists for the user.
     */
    boolean existsByUserIdAndCurrency(Long userId, Currency currency);

    /**
     * Retrieves a wallet with a pessimistic write lock for safe balance modification.
     * <p>
     * This lock prevents concurrent transactions (e.g., two simultaneous withdrawals)
     * from reading stale balance data. The lock is held until the enclosing transaction commits.
     * </p>
     *
     * @param id     The unique identifier of the wallet.
     * @param userId The unique identifier of the wallet owner.
     * @return An {@link Optional} containing the locked wallet if found and owned by the user.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id AND w.user.id = :userId")
    Optional<Wallet> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);
}
