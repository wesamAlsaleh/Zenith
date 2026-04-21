package com.avocadogroup.zenith.lock;

import com.avocadogroup.zenith.lock.dtos.TestRequest;
import com.avocadogroup.zenith.lock.dtos.TestResponse;
import com.avocadogroup.zenith.wallets.WalletService;
import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import com.avocadogroup.zenith.wallets.dtos.WalletTransactionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/test")
@AllArgsConstructor
public class LockController {
    private final WalletService walletService;

    /**
     * Tests concurrent access to wallet operations with thread synchronization.
     * 
     * @param request Test configuration containing user ID, wallet ID, operation type,
     *                amount, and number of threads
     * @return TestResponse with final balance and statistics
     * @throws InterruptedException if thread execution is interrupted
     */
    @PostMapping("/concurrency")
    public ResponseEntity<?> testConcurrency(@RequestBody TestRequest request) throws InterruptedException {
        // Get the number of threads for the test
        var threads = request.getThreads();

        // Create a thread pool with the specified number of threads
        var executor = Executors.newFixedThreadPool(threads);

        // Holds all threads until the count becomes 0
        var startLatch = new CountDownLatch(1);

        // Waits for all threads to finish (waits for the count to become zero)
        var doneLatch = new CountDownLatch(threads); // Allows one or more threads to wait until a set of operations performed by other threads completes

        // Statistics counter (thread safety)
        var successCount = new AtomicInteger(0);
        var failCount = new AtomicInteger(0);

        // Do the operation n threads time
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    // Blocks the calling thread until count becomes zero
                    startLatch.await();

                    // Get the thread name
                    var threadName = Thread.currentThread().getName();

                    // Get the start time of the operation
                    var startTime = System.currentTimeMillis();

                    // Build the transaction request
                    var walletTransactionRequest = new WalletTransactionRequest();
                    walletTransactionRequest.setAmount(request.getAmount());

                    // Init the result placeholder
                    WalletDto result;

                    // Do the operation bases the operation type
                    if ("DEPOSIT".equalsIgnoreCase(request.getOperation())) {
                        // Deposit to the wallet
                        result = walletService.deposit(
                                request.getUserId(),
                                request.getWalletId(),
                                walletTransactionRequest
                        );
                    } else {
                        // Withdraw from the wallet
                        result = walletService.withdraw(
                                request.getUserId(),
                                request.getWalletId(),
                                walletTransactionRequest
                        );
                    }

                    // Get the end time of the operation
                    var endTime = System.currentTimeMillis();

                    log.info("[{}] {} | Duration: {}ms | Balance after: {}",
                            threadName, request.getOperation(), (endTime - startTime), result.getBalance());

                    // Increment the success count
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Increment the failure count
                    failCount.incrementAndGet();
                    log.error("[{}] FAILED: {}", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    // Decrement the done latch (reduces the count by 1)
                    doneLatch.countDown();
                }
            });
        }

        // Release all the threads at the exact same moment in the pool
        startLatch.countDown(); // Reduces the count by 1 to be zero

        // Wait for all threads to finish (waits for the count to become zero to shut down)
        doneLatch.await();

        // Shut down the thread pool
        executor.shutdown();

        // Fetch the wallet balance from the db
        var finalWallet = walletService.getWallet(request.getUserId(), request.getWalletId());

        log.info("=== TEST COMPLETE === Final balance: {} | Success: {} | Failed: {}",
                finalWallet.getBalance(), successCount.get(), failCount.get());

        // Return Response DTO
        return ResponseEntity.ok(new TestResponse(
                finalWallet.getBalance(),
                successCount.get(),
                failCount.get()
        ));
    }
}
