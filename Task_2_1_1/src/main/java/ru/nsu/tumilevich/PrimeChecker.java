package ru.nsu.tumilevich;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс для лабораторной по многопоточке.
 * Задача: найти хотя бы одно непростое (составное) число в массиве тремя разными способами.
 */
public class PrimeChecker {

    /**
     * Проверка числа на простоту.
     * @param number проверяемое число
     * @return true если число простое, иначе false
     */
    static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;

        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

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

    public static void main(String[] args) throws InterruptedException {
        // --- 1. Проверка примеров ---
        int[] test1 = {6, 8, 7, 13, 5, 9, 4};
        System.out.println("Тест 1 [6, 8, ... 4]: " + hasNonPrimeSequential(test1));

        int[] test2 = {20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                6998009, 6998029, 6998039, 20165149, 6998051, 6998053};
        System.out.println("Тест 2 [20319251, ...]: " + hasNonPrimeSequential(test2));
        System.out.println("-------------------------------------------------");

        // --- 2. Сбор метрик для графика ---
        // Забиваем массив максимальными простыми числами, чтобы загрузить процессор по полной
        int dataSize = 200_000;
        int[] largePrimeArray = new int[dataSize];
        Arrays.fill(largePrimeArray, 2147483647);


        hasNonPrimeSequential(test2);
        hasNonPrimeParallelStream(test2);

        System.out.println("Начало бенчмарка (массив из " + dataSize + " больших простых чисел):");


        long startTime = System.currentTimeMillis();
        hasNonPrimeSequential(largePrimeArray);
        long timeSequential = System.currentTimeMillis() - startTime;
        System.out.println("1) Последовательное: " + timeSequential + " мс");


        int[] threadCounts = {2, 4, 6, 8};
        for (int threads : threadCounts) {
            startTime = System.currentTimeMillis();
            hasNonPrimeThreads(largePrimeArray, threads);
            long timeThreads = System.currentTimeMillis() - startTime;
            System.out.println("2) Thread (" + threads + " потоков): " + timeThreads + " мс");
        }

        // Замер параллельных стримов
        startTime = System.currentTimeMillis();
        hasNonPrimeParallelStream(largePrimeArray);
        long timeParallelStream = System.currentTimeMillis() - startTime;
        System.out.println("3) parallelStream: " + timeParallelStream + " мс");
    }
}