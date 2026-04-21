package com.avocadogroup.zenith.transactions;

import com.avocadogroup.zenith.transactions.dtos.TransactionDto;
import com.avocadogroup.zenith.wallets.Wallet;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

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
        // Fetch the user transactions by wallet id
        var transactions = transactionRepository.findByWalletId(walletId, pageable);

        // Map each transaction to DTO
        var transactionsDto = transactions.stream()
                .map(transactionMapper::toDto)
                .toList();

        // Return the paginated result as Page<TransactionDto>
        return new PageImpl<>(transactionsDto, pageable, transactionsDto.size());
    }
}
