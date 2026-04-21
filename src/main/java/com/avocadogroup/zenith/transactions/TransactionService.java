package com.avocadogroup.zenith.transactions;

import com.avocadogroup.zenith.transactions.dtos.TransactionDto;
import com.avocadogroup.zenith.wallets.Wallet;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Create a transaction record
     *
     * @param senderWallet   The wallet that sent the funds
     * @param receiverWallet The wallet that received the funds
     * @param amount         The transaction amount
     * @param type           The type of transaction
     * @return The saved Transaction entity
     */
    public TransactionDto recordTransaction(
            Wallet senderWallet,
            Wallet receiverWallet,
            BigDecimal amount,
            TransactionType type
    ) {
        // Create a new transaction entity
        Transaction transaction = new Transaction();

        // Set the transaction properties
        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setStatus(TransactionStatus.SUCCESS.toString());

        // Save the changes and return the saved transaction as Dto
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    /**
     * Retrieves paginated transactions for a user, with optional type filtering.
     *
     * @param walletId The ID of the wallet
     * @param type     Optional transaction type filter 
     * @param pageable Pagination and sorting parameters (e.g., page number, size, sort order)
     * @return A page of TransactionDto results
     */
    public Page<TransactionDto> getTransactions(
            Long walletId,
            TransactionType type,
            Pageable pageable
    ) {
        // Fetch transactions, filter by type if provided otherwise fetch all for the wallet
        Page<Transaction> transactions;

        if (type != null) {
            transactions = transactionRepository.findByWalletIdAndType(walletId, type, pageable);
        } else {
            transactions = transactionRepository.findByWalletId(walletId, pageable);
        }

        // Map entities to DTOs while preserving pagination metadata
        return transactions.map(transactionMapper::toDto);
    }
}
