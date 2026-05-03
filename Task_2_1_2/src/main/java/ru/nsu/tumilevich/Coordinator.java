package ru.nsu.tumilevich;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class Coordinator {

    private static final int PORT = 8080;
    private static final int WORKER_TIMEOUT_MS = 5000; // 5 seconds timeout for worker response

    private final List<WorkerConnection> workerConnections = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService workerTaskExecutor = Executors.newCachedThreadPool(); // For handling worker communication
    private final AtomicBoolean nonPrimeFoundGlobal = new AtomicBoolean(false);
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public Coordinator() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Coordinator initialized on port " + PORT);
        } catch (IOException e) {
            System.err.println("Failed to initialize Coordinator ServerSocket: " + e.getMessage());
            System.exit(1);
        }
    }

    public void startAcceptingConnections() {
        new Thread(() -> {
            try {
                while (running && !serverSocket.isClosed()) {
                    Socket workerSocket = serverSocket.accept();
                    System.out.println("Worker connected: " + workerSocket.getInetAddress());
                    WorkerConnection workerConnection = new WorkerConnection(workerSocket);
                    workerConnections.add(workerConnection);
                }
            } catch (IOException e) {
                if (running && !serverSocket.isClosed()) {
                    System.err.println("Error accepting worker connections: " + e.getMessage());
                }
            }
        }, "Coordinator-Acceptor-Thread").start();
    }

    public boolean distributeAndCollect(int[] numbers) {
        if (workerConnections.isEmpty()) {
            System.err.println("No workers connected. Cannot distribute tasks.");
            return false;
        }

        nonPrimeFoundGlobal.set(false);
        int chunkSize = (numbers.length + workerConnections.size() - 1) / workerConnections.size();
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < workerConnections.size(); i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);

            if (start < numbers.length) {
                int[] chunk = Arrays.copyOfRange(numbers, start, end);
                WorkerConnection worker = workerConnections.get(i);
                results.add(workerTaskExecutor.submit(() -> {
                    try {
                        return worker.sendChunkAndGetResult(chunk);
                    } catch (IOException | ClassNotFoundException | TimeoutException e) {
                        System.err.println("Error communicating with worker " + worker.socket.getInetAddress() + ": " + e.getMessage());
                        return false;
                    }
                }));
            }
        }

        for (Future<Boolean> result : results) {
            try {
                if (result.get()) {
                    nonPrimeFoundGlobal.set(true);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error getting result from worker: " + e.getMessage());
            }
        }
        return nonPrimeFoundGlobal.get();
    }

    public void shutdown() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        workerTaskExecutor.shutdownNow();
        for (WorkerConnection worker : workerConnections) {
            try {
                worker.socket.close();
            } catch (IOException e) {
                System.err.println("Error closing worker socket: " + e.getMessage());
            }
        }
        System.out.println("Coordinator shut down.");
    }

    public static void main(String[] args) {
        Coordinator coordinator = new Coordinator();
        coordinator.startAcceptingConnections();
        // Keep coordinator running indefinitely or until a shutdown signal
        try {
            Thread.currentThread().join(); // Keep main thread alive
        } catch (InterruptedException e) {
            System.out.println("Coordinator main thread interrupted.");
        } finally {
            coordinator.shutdown();
        }
    }

    private class WorkerConnection {
        private final Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public WorkerConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }

        public boolean sendChunkAndGetResult(int[] chunk) throws IOException, ClassNotFoundException, TimeoutException {
            out.writeObject(chunk);
            out.flush();

            Future<Boolean> futureResult = workerTaskExecutor.submit(() -> (Boolean) in.readObject());
            try {
                return futureResult.get(WORKER_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException("Failed to get result from worker", e);
            } catch (TimeoutException e) {
                throw new TimeoutException("Worker timed out.");
            }
        }
    }
}
