package com.avocadogroup.zenith.transactions;

import com.avocadogroup.zenith.authentication.services.AuthenticationService;
import com.avocadogroup.zenith.transactions.dtos.TransactionDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for transaction retrieval.
 * All endpoints require authentication.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;

    /**
     * Retrieves paginated transactions for the authenticated user
     * 
     * @param type Optional transaction type filter (e.g., DEPOSIT, WITHDRAW)
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 10)
     * @return A paginated list of TransactionDto
     */
    @GetMapping
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Get the authenticated user's ID from the security context
        Long userId = authenticationService.getUserId();

        // Build pageable with default sort by createdAt descending
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Fetch paginated transactions
        Page<TransactionDto> transactions = transactionService.getTransactions(userId, type, pageable);

        // Return HTTP 200 OK with the paginated transaction list
        return ResponseEntity.ok(transactions);
    }
}
