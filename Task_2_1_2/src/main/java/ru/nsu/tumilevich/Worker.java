package ru.nsu.tumilevich;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Worker {

    private static final String COORDINATOR_HOST = "localhost"; // Assuming coordinator runs on the same machine for testing
    private static final int COORDINATOR_PORT = 8080;

    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.start();
    }

    public void start() {
        System.out.println("Worker started. Connecting to coordinator at " + COORDINATOR_HOST + ":" + COORDINATOR_PORT);
        try (Socket socket = new Socket(COORDINATOR_HOST, COORDINATOR_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to coordinator.");

            while (true) { // Keep listening for tasks
                try {
                    int[] chunk = (int[]) in.readObject();
                    System.out.println("Received chunk of size: " + chunk.length);
                    boolean nonPrimeFound = hasNonPrimeInChunk(chunk);
                    out.writeObject(nonPrimeFound);
                    out.flush();
                    System.out.println("Sent result: " + nonPrimeFound);
                } catch (ClassNotFoundException e) {
                    System.err.println("Error reading object from coordinator: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Coordinator disconnected or I/O error: " + e.getMessage());
                    break; // Exit loop if coordinator disconnects
                }
            }

        } catch (IOException e) {
            System.err.println("Could not connect to coordinator or I/O error: " + e.getMessage());
        }
    }

    // Reusing the isPrime logic from the first task
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

    private boolean hasNonPrimeInChunk(int[] numbers) {
        for (int n : numbers) {
            if (!isPrime(n)) {
                return true;
            }
        }
        return false;
    }
}
