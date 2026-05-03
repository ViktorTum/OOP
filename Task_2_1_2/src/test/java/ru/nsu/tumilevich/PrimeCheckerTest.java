package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class PrimeCheckerTest {

    // Test cases for isPrime method
    @Test
    void testIsPrime() {
        assertTrue(PrimeChecker.isPrime(2));
        assertTrue(PrimeChecker.isPrime(3));
        assertFalse(PrimeChecker.isPrime(4));
        assertTrue(PrimeChecker.isPrime(5));
        assertFalse(PrimeChecker.isPrime(6));
        assertTrue(PrimeChecker.isPrime(7));
        assertFalse(PrimeChecker.isPrime(9));
        assertTrue(PrimeChecker.isPrime(17));
        assertTrue(PrimeChecker.isPrime(2147483647)); // LARGE_PRIME
        assertFalse(PrimeChecker.isPrime(1));
        assertFalse(PrimeChecker.isPrime(0));
        assertFalse(PrimeChecker.isPrime(-5));
        assertFalse(PrimeChecker.isPrime(100));
        assertFalse(PrimeChecker.isPrime(999999938)); // A large non-prime
    }

    // Test cases for hasNonPrimeSequential
    @Test
    void testHasNonPrimeSequential() {
        // All primes
        int[] allPrimes = {2, 3, 5, 7, 17};
        assertFalse(PrimeChecker.hasNonPrimeSequential(allPrimes));

        // Contains one non-prime
        int[] oneNonPrime = {2, 3, 4, 5, 7};
        assertTrue(PrimeChecker.hasNonPrimeSequential(oneNonPrime));

        // Contains multiple non-primes
        int[] multipleNonPrimes = {2, 4, 6, 8, 10};
        assertTrue(PrimeChecker.hasNonPrimeSequential(multipleNonPrimes));

        // Empty array
        int[] emptyArray = {};
        assertFalse(PrimeChecker.hasNonPrimeSequential(emptyArray));

        // Array with 1 (non-prime)
        int[] arrayWithOne = {1};
        assertTrue(PrimeChecker.hasNonPrimeSequential(arrayWithOne));

        // Array with 0 (non-prime)
        int[] arrayWithZero = {0};
        assertTrue(PrimeChecker.hasNonPrimeSequential(arrayWithZero));

        // Large array of primes
        int[] largePrimes = new int[1000];
        Arrays.fill(largePrimes, 2147483647); // LARGE_PRIME
        assertFalse(PrimeChecker.hasNonPrimeSequential(largePrimes));

        // Large array with one non-prime
        int[] largeWithNonPrime = new int[1000];
        Arrays.fill(largeWithNonPrime, 2147483647);
        largeWithNonPrime[500] = 4;
        assertTrue(PrimeChecker.hasNonPrimeSequential(largeWithNonPrime));
    }

    // Test cases for hasNonPrimeThreads
    @Test
    void testHasNonPrimeThreads() throws InterruptedException {
        // All primes
        int[] allPrimes = {2, 3, 5, 7, 17};
        assertFalse(PrimeChecker.hasNonPrimeThreads(allPrimes, 4));

        // Contains one non-prime
        int[] oneNonPrime = {2, 3, 4, 5, 7};
        assertTrue(PrimeChecker.hasNonPrimeThreads(oneNonPrime, 4));

        // Contains multiple non-primes
        int[] multipleNonPrimes = {2, 4, 6, 8, 10};
        assertTrue(PrimeChecker.hasNonPrimeThreads(multipleNonPrimes, 4));

        // Empty array
        int[] emptyArray = {};
        assertFalse(PrimeChecker.hasNonPrimeThreads(emptyArray, 4));

        // Array with 1 (non-prime)
        int[] arrayWithOne = {1};
        assertTrue(PrimeChecker.hasNonPrimeThreads(arrayWithOne, 1));

        // Large array of primes
        int[] largePrimes = new int[1000];
        Arrays.fill(largePrimes, 2147483647); // LARGE_PRIME
        assertFalse(PrimeChecker.hasNonPrimeThreads(largePrimes, 8));

        // Large array with one non-prime
        int[] largeWithNonPrime = new int[1000];
        Arrays.fill(largeWithNonPrime, 2147483647);
        largeWithNonPrime[500] = 4;
        assertTrue(PrimeChecker.hasNonPrimeThreads(largeWithNonPrime, 8));
    }

    // Test cases for hasNonPrimeParallelStream
    @Test
    void testHasNonPrimeParallelStream() {
        // All primes
        int[] allPrimes = {2, 3, 5, 7, 17};
        assertFalse(PrimeChecker.hasNonPrimeParallelStream(allPrimes));

        // Contains one non-prime
        int[] oneNonPrime = {2, 3, 4, 5, 7};
        assertTrue(PrimeChecker.hasNonPrimeParallelStream(oneNonPrime));

        // Contains multiple non-primes
        int[] multipleNonPrimes = {2, 4, 6, 8, 10};
        assertTrue(PrimeChecker.hasNonPrimeParallelStream(multipleNonPrimes));

        // Empty array
        int[] emptyArray = {};
        assertFalse(PrimeChecker.hasNonPrimeParallelStream(emptyArray));

        // Array with 1 (non-prime)
        int[] arrayWithOne = {1};
        assertTrue(PrimeChecker.hasNonPrimeParallelStream(arrayWithOne));

        // Large array of primes
        int[] largePrimes = new int[1000];
        Arrays.fill(largePrimes, 2147483647); // LARGE_PRIME
        assertFalse(PrimeChecker.hasNonPrimeParallelStream(largePrimes));

        // Large array with one non-prime
        int[] largeWithNonPrime = new int[1000];
        Arrays.fill(largeWithNonPrime, 2147483647);
        largeWithNonPrime[500] = 4;
        assertTrue(PrimeChecker.hasNonPrimeParallelStream(largeWithNonPrime));
    }
}
