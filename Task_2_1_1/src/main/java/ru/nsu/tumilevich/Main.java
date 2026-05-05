package ru.nsu.tumilevich;

import java.util.Arrays;

import static ru.nsu.tumilevich.PrimeChecker.hasNonPrimeParallelStream;
import static ru.nsu.tumilevich.PrimeChecker.hasNonPrimeSequential;
import static ru.nsu.tumilevich.PrimeChecker.hasNonPrimeThreads;

public class Main {
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
