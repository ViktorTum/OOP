package ru.nsu.tumilevich;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.nsu.tumilevich.PrimeUtil.isPrime;

/**
 * Класс для лабораторной по многопоточке.
 * Задача: найти хотя бы одно непростое (составное) число в массиве тремя разными способами.
 */
public class PrimeChecker {

    /**
     * 1. Обычный последовательный перебор массива в один поток.
     * @param numbers входной массив
     * @return true, если нашли непростое число
     */
    static boolean hasNonPrimeSequential(int[] numbers) {
        for (int n : numbers) {
            if (!isPrime(n)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 2. Многопоточный вариант ручками через Thread.
     *
     * @param numbers    массив для проверки
     * @param numThreads количество потоков
     * @return true, если нашли непростое число
     * @throws InterruptedException если потоки прервут во время join
     */
    static boolean hasNonPrimeThreads(int[] numbers, int numThreads) throws InterruptedException {
        AtomicBoolean foundNonPrime = new AtomicBoolean(false);
        Thread[] threads = new Thread[numThreads];

        int chunkSize = (numbers.length + numThreads - 1) / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (foundNonPrime.get()) return;

                    if (!isPrime(numbers[j])) {
                        foundNonPrime.set(true);
                        return;
                    }
                }
            });

            if (start < numbers.length) {
                threads[i].start();
            }
        }

        for (int i = 0; i < numThreads; i++) {
            if (threads[i] != null && threads[i].isAlive()) {
                threads[i].join();
            }
        }

        return foundNonPrime.get();
    }

    /**
     * 3. Вариант через Stream API.
     *
     * @param numbers входной массив
     * @return true, если есть непростое
     */
    static boolean hasNonPrimeParallelStream(int[] numbers) {
        return Arrays.stream(numbers)
                .parallel()
                .anyMatch(n -> !isPrime(n));
    }
}