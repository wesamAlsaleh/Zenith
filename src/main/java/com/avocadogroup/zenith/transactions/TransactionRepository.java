package com.avocadogroup.zenith.transactions;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    // Fetch all transactions where the wallet is either the sender or receiver
    @Query("SELECT t FROM Transaction t WHERE t.senderWallet.id = :walletId OR t.receiverWallet.id = :walletId")
    Page<Transaction> findByWalletId(Long walletId, Pageable pageable);

    // Fetch transactions by wallet id and transaction type
    @Query("SELECT t FROM Transaction t WHERE (t.senderWallet.id = :walletId OR t.receiverWallet.id = :walletId) AND t.transactionType = :type")
    Page<Transaction> findByWalletIdAndType(Long walletId, TransactionType type, Pageable pageable);
}
