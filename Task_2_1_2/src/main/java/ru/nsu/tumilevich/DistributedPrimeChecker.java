package ru.nsu.tumilevich;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class DistributedPrimeChecker {

    private static final int NUM_TESTS = 50; // Fewer tests for distributed to save time

    // Максимальное простое int, чтобы грузить проц
    private static final int LARGE_PRIME = 2147483647;
    // Простое поменьше, чтобы грузить меньше
    private static final int SMALL_PRIME = 17;

    public static void main(String[] args) throws InterruptedException {
        // Start Coordinator in a separate thread
        Thread coordinatorThread = new Thread(() -> {
            Coordinator coordinator = new Coordinator();
            coordinator.startAcceptingConnections();
        });
        coordinatorThread.start();

        // Give coordinator some time to start up
        Thread.sleep(2000);

        // Start a few workers (e.g., 2 workers for demonstration)
        // In a real scenario, these would be separate processes on different machines
        Thread worker1Thread = new Thread(() -> {
            Worker worker = new Worker();
            worker.start();
        });
        worker1Thread.start();

        Thread worker2Thread = new Thread(() -> {
            Worker worker = new Worker();
            worker.start();
        });
        worker2Thread.start();

        // Give workers some time to connect to coordinator
        Thread.sleep(2000);

        // Размеры массивов
        int smallSize = 500;    // Соответствует 'small'
        int largeSize = 250_000; // Соответствует 'big'

        int[] arrSmallSmall = new int[smallSize];
        Arrays.fill(arrSmallSmall, SMALL_PRIME); // smallsmall.txt

        int[] arrSmallLarge = new int[smallSize];
        Arrays.fill(arrSmallLarge, LARGE_PRIME); // smallbig.txt

        int[] arrLargeSmall = new int[largeSize];
        Arrays.fill(arrLargeSmall, SMALL_PRIME); // bigsmall.txt

        int[] arrLargeLarge = new int[largeSize];
        Arrays.fill(arrLargeLarge, LARGE_PRIME); // bigbig.txt

        System.out.println("Старт глобального бенчмарка для распределенной системы...");

        String fileName = "distributed_benchmark_data.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Array_Category;Algorithm;Threads;Test_Run_Index;Time_ns");

            // For distributed, we'll simulate different 'thread' counts by having more workers
            // For this example, we'll just run with the connected workers
            runDistributedBenchmark(writer, "smallsmall.txt", arrSmallSmall);
            runDistributedBenchmark(writer, "smallbig.txt", arrSmallLarge);
            runDistributedBenchmark(writer, "bigsmall.txt", arrLargeSmall);
            runDistributedBenchmark(writer, "bigbig.txt", arrLargeLarge);

            System.out.println("Бенчмарк распределенной системы успешно завершен! Все данные лежат в файле " + fileName);

        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
        } finally {
            // In a real scenario, you'd have a graceful shutdown for coordinator and workers
            // For this example, we'll just let the main thread exit.
            coordinatorThread.interrupt();
            worker1Thread.interrupt();
            worker2Thread.interrupt();
        }
    }

    private static void runDistributedBenchmark(PrintWriter writer, String categoryName, int[] array) throws InterruptedException {
        System.out.println("Запуск распределенных тестов для: " + categoryName + "...");
        Coordinator clientCoordinator = new Coordinator(); // Client-side coordinator to send tasks

        for (int run = 0; run < NUM_TESTS; run++) {
            long startTime = System.nanoTime();
            // This will connect to the running coordinator and distribute tasks
            clientCoordinator.distributeAndCollect(array);
            long timeDistributed = System.nanoTime() - startTime;
            writer.println(categoryName + ";Distributed;Auto;" + (run + 1) + ";" + timeDistributed);
        }
        System.out.println("Готово для распределенных тестов для: " + categoryName + ".\n");
    }
}
