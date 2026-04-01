package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrimeCheckerTest {

	@Test
	void testIsPrime_EdgeCases() {
		assertFalse(PrimeChecker.isPrime(-5));
		assertFalse(PrimeChecker.isPrime(0));
		assertFalse(PrimeChecker.isPrime(1));
	}

	@Test
	void testIsPrime_Primes() {
		assertTrue(PrimeChecker.isPrime(2));
		assertTrue(PrimeChecker.isPrime(3));
		assertTrue(PrimeChecker.isPrime(7));

		assertTrue(PrimeChecker.isPrime(20319251));
	}

	@Test
	void testIsPrime_NonPrimes() {
		assertFalse(PrimeChecker.isPrime(4));
		assertFalse(PrimeChecker.isPrime(100));

		assertFalse(PrimeChecker.isPrime(9));
		assertFalse(PrimeChecker.isPrime(15));
	}

	@Test
	void testHasNonPrimeSequential() {
		int[] allPrimes = {2, 3, 5, 7, 20319251};
		int[] hasNonPrime = {2, 3, 4, 5};
		int[] empty = {};

		assertFalse(PrimeChecker.hasNonPrimeSequential(allPrimes));
		assertTrue(PrimeChecker.hasNonPrimeSequential(hasNonPrime));
		assertFalse(PrimeChecker.hasNonPrimeSequential(empty));
	}

	@Test
	void testHasNonPrimeThreads_Standard() throws InterruptedException {
		int[] allPrimes = {2, 3, 5, 7, 11, 13, 17, 19};
		int[] hasNonPrime = {2, 3, 5, 7, 9, 11}; // 9 - непростое
		int[] empty = {};

		assertFalse(PrimeChecker.hasNonPrimeThreads(allPrimes, 2));
		assertTrue(PrimeChecker.hasNonPrimeThreads(hasNonPrime, 2));
		assertFalse(PrimeChecker.hasNonPrimeThreads(empty, 2));
	}

	@Test
	void testHasNonPrimeThreads_MoreThreadsThanElements() throws InterruptedException {
		int[] smallArrayPrimes = {2, 3};
		int[] smallArrayHasNonPrime = {2, 4};

		assertFalse(PrimeChecker.hasNonPrimeThreads(smallArrayPrimes, 10));
		assertTrue(PrimeChecker.hasNonPrimeThreads(smallArrayHasNonPrime, 10));
	}

	@Test
	void testHasNonPrimeParallelStream() {
		int[] allPrimes = {2, 3, 5, 7, 20319251};
		int[] hasNonPrime = {2, 3, 4, 5};
		int[] empty = {};

		assertFalse(PrimeChecker.hasNonPrimeParallelStream(allPrimes));
		assertTrue(PrimeChecker.hasNonPrimeParallelStream(hasNonPrime));
		assertFalse(PrimeChecker.hasNonPrimeParallelStream(empty));
	}

	@Test
	void testTaskExamples() throws InterruptedException {
		int[] task1 = {6, 8, 7, 13, 5, 9, 4};
		int[] task2 = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
				6998009, 6998029, 6998039, 20165149, 6998051, 6998053};

		assertTrue(PrimeChecker.hasNonPrimeSequential(task1));
		assertFalse(PrimeChecker.hasNonPrimeSequential(task2));

		assertTrue(PrimeChecker.hasNonPrimeThreads(task1, 4));
		assertFalse(PrimeChecker.hasNonPrimeThreads(task2, 4));

		assertTrue(PrimeChecker.hasNonPrimeParallelStream(task1));
		assertFalse(PrimeChecker.hasNonPrimeParallelStream(task2));
	}
}