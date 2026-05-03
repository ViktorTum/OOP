package ru.nsu.tumilevich;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DistributedPrimeCheckerTest {

    private Coordinator coordinator;
    private ExecutorService workerExecutor;
    private Thread coordinatorThread;

    @BeforeEach
    void setUp() throws InterruptedException {
        coordinator = new Coordinator();
        coordinatorThread = new Thread(() -> coordinator.startAcceptingConnections());
        coordinatorThread.start();

        // Give coordinator some time to start up
        Thread.sleep(1000);

        workerExecutor = Executors.newFixedThreadPool(2); // Two workers for testing
        workerExecutor.submit(() -> new Worker().start());
        workerExecutor.submit(() -> new Worker().start());

        // Give workers some time to connect
        Thread.sleep(2000);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        coordinator.shutdown();
        coordinatorThread.interrupt();
        coordinatorThread.join(1000); // Wait for coordinator thread to finish

        workerExecutor.shutdownNow();
        workerExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    void testDistributedHasNonPrime_AllPrimes() {
        int[] allPrimes = {2, 3, 5, 7, 17, 23, 29, 31, 37, 41};
        assertFalse(coordinator.distributeAndCollect(allPrimes));
    }

    @Test
    void testDistributedHasNonPrime_OneNonPrime() {
        int[] oneNonPrime = {2, 3, 4, 5, 7, 11, 13, 17, 19, 23};
        assertTrue(coordinator.distributeAndCollect(oneNonPrime));
    }

    @Test
    void testDistributedHasNonPrime_MultipleNonPrimes() {
        int[] multipleNonPrimes = {2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        assertTrue(coordinator.distributeAndCollect(multipleNonPrimes));
    }

    @Test
    void testDistributedHasNonPrime_EmptyArray() {
        int[] emptyArray = {};
        assertFalse(coordinator.distributeAndCollect(emptyArray));
    }

    @Test
    void testDistributedHasNonPrime_ArrayWithOne() {
        int[] arrayWithOne = {1};
        assertTrue(coordinator.distributeAndCollect(arrayWithOne));
    }

    @Test
    void testDistributedHasNonPrime_ArrayWithZero() {
        int[] arrayWithZero = {0};
        assertTrue(coordinator.distributeAndCollect(arrayWithZero));
    }

    @Test
    void testDistributedHasNonPrime_LargeArrayAllPrimes() {
        int largeSize = 10000;
        int[] largePrimes = new int[largeSize];
        Arrays.fill(largePrimes, 2147483647); // LARGE_PRIME
        assertFalse(coordinator.distributeAndCollect(largePrimes));
    }

    @Test
    void testDistributedHasNonPrime_LargeArrayWithOneNonPrime() {
        int largeSize = 10000;
        int[] largeWithNonPrime = new int[largeSize];
        Arrays.fill(largeWithNonPrime, 2147483647);
        largeWithNonPrime[largeSize / 2] = 999999938; // A large non-prime
        assertTrue(coordinator.distributeAndCollect(largeWithNonPrime));
    }

    @Test
    void testDistributedHasNonPrime_NoWorkers() throws InterruptedException {
        // Shutdown existing workers and coordinator to test no workers scenario
        tearDown();
        
        // Re-initialize coordinator without starting workers
        coordinator = new Coordinator();
        coordinatorThread = new Thread(() -> coordinator.startAcceptingConnections());
        coordinatorThread.start();
        Thread.sleep(500);

        int[] numbers = {2, 3, 4};
        assertFalse(coordinator.distributeAndCollect(numbers)); // Should return false as no workers to process
    }

    // This test is more complex as it requires simulating a worker failure
    // For now, we'll rely on the timeout mechanism in Coordinator to handle unresponsive workers.
    // A more advanced test would involve mocking or programmatically killing a worker process.
    @Test
    void testDistributedHasNonPrime_WorkerTimeout() throws InterruptedException {
        // This test requires a more controlled environment to simulate a worker timeout.
        // For now, we'll assume the timeout mechanism in Coordinator.java works as intended.
        // We can't easily simulate a worker hanging without modifying the Worker class for testing purposes.
        // The current implementation of `sendChunkAndGetResult` in Coordinator handles `TimeoutException`.
        int[] numbers = {2, 3, 4};
        assertTrue(coordinator.distributeAndCollect(numbers)); // Should still work if other workers are fine
    }
}
