package ru.nsu.tumilevich;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Управляющий класс (движок) пиццерии.
 * Отвечает за инициализацию очередей, запуск/остановку потоков пекарей и курьеров,
 * а также за сериализацию и десериализацию незавершенных заказов при перезапусках.
 */
public class PizzeriaEngine {
    private final PizzeriaConfig config;
    private final BlockingQueue<Order> orderQueue;
    private final BlockingQueue<Order> stock;
    private final List<Thread> workerThreads = new ArrayList<>();

    private final List<Baker> bakers = new ArrayList<>();
    private final List<Courier> couriers = new ArrayList<>();

    private final String SAVE_FILE = "unfinished_orders.ser";

    /**
     * Создает движок пиццерии на основе переданной конфигурации.
     *
     * @param config объект конфигурации с параметрами склада, пекарей и курьеров
     */
    public PizzeriaEngine(PizzeriaConfig config) {
        this.config = config;
        this.orderQueue = new BlockingQueue<>(1000);
        this.stock = new BlockingQueue<>(config.getStockCapacity());
    }

    public int start() {
        int maxOrderId = loadUnfinishedOrders();

        for (int i = 0; i < config.getBakers().size(); i++) {
            Baker baker = new Baker(i, config.getBakers().get(i).getCookingTime(), orderQueue, stock);
            bakers.add(baker);
            Thread t = new Thread(baker, "Baker-" + i);
            workerThreads.add(t);
            t.start();
        }

        for (int i = 0; i < config.getCouriers().size(); i++) {
            PizzeriaConfig.CourierConfig courierConfig = config.getCouriers().get(i);
            Courier courier = new Courier(i, courierConfig.getTrunkCapacity(), courierConfig.getDeliveryTime(), stock);
            couriers.add(courier);
            Thread t = new Thread(courier, "Courier-" + i);
            workerThreads.add(t);
            t.start();
        }

        return maxOrderId;
    }

    public void addNewOrder(int orderId) {
        try {
            orderQueue.put(new Order(orderId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        System.out.println("Stopping pizzeria...");
        orderQueue.close();
        stock.close();

        for (Thread t : workerThreads) {
            t.interrupt();
        }

        for (Thread t : workerThreads) {
            try {
                t.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        saveUnfinishedOrders();
    }

    private void saveUnfinishedOrders() {
        List<Order> unfinished = new ArrayList<>();
        unfinished.addAll(orderQueue.getAll());
        unfinished.addAll(stock.getAll());

        for (Baker baker : bakers) {
            if (baker.getCurrentOrder() != null) {
                unfinished.add(baker.getCurrentOrder());
            }
        }
        for (Courier courier : couriers) {
            unfinished.addAll(courier.getCurrentOrders());
        }

        if (!unfinished.isEmpty()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
                oos.writeObject(unfinished);
                System.out.println("Saved " + unfinished.size() + " unfinished orders.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private int loadUnfinishedOrders() {
        int maxId = 0;
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Order> unfinished = (List<Order>) ois.readObject();
                for (Order order : unfinished) {
                    if (order.getId() > maxId) {
                        maxId = order.getId();
                    }
                    if (order.getStatus() == Order.OrderStatus.COOKING || order.getStatus() == Order.OrderStatus.PENDING) {
                        order.setStatus(Order.OrderStatus.PENDING);
                        orderQueue.put(order);
                    } else {
                        order.setStatus(Order.OrderStatus.IN_STOCK);
                        stock.put(order);
                    }
                }
                System.out.println("Loaded " + unfinished.size() + " unfinished orders.");
                file.delete();
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return maxId;
    }
}