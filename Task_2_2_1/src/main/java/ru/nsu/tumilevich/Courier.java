package ru.nsu.tumilevich;

import java.util.ArrayList;
import java.util.List;

/**
 * Представляет курьера (worker-поток), который забирает готовые пиццы со склада
 * и доставляет их. Курьер имеет ограниченную вместимость багажника.
 */
public class Courier implements Runnable {
    private final int id;
    private final int trunkCapacity;
    private final int deliveryTime;
    private final BlockingQueue<Order> stock;

    private final List<Order> currentOrders = new ArrayList<>();

    /**
     * Создает нового курьера.
     *
     * @param id            уникальный идентификатор курьера
     * @param trunkCapacity вместимость багажника (максимальное количество пицц за рейс)
     * @param deliveryTime  время, затрачиваемое на один рейс доставки (в миллисекундах)
     * @param stock         склад (очередь), откуда курьер забирает пиццы
     */
    public Courier(int id, int trunkCapacity, int deliveryTime, BlockingQueue<Order> stock) {
        this.id = id;
        this.trunkCapacity = trunkCapacity;
        this.deliveryTime = deliveryTime;
        this.stock = stock;
    }

    /**
     * Основной цикл работы курьера. Курьер блокируется в ожидании хотя бы одного заказа,
     * затем неблокирующим образом добирает пиццы до заполнения багажника и имитирует доставку.
     * При прерывании потока сохраняет недоставленные заказы.
     */
    @Override
    public void run() {
        try {
            while (true) {
                currentOrders.clear();

                Order firstOrder = stock.take();
                if (firstOrder == null) break;
                currentOrders.add(firstOrder);

                while (currentOrders.size() < trunkCapacity) {
                    Order nextOrder = stock.poll();
                    if (nextOrder != null) {
                        currentOrders.add(nextOrder);
                    } else {
                        break;
                    }
                }

                for (Order order : currentOrders) {
                    order.setStatus(Order.OrderStatus.DELIVERING);
                }

                Thread.sleep(deliveryTime);

                for (Order order : currentOrders) {
                    order.setStatus(Order.OrderStatus.DELIVERED);
                }
                currentOrders.clear();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<Order> getCurrentOrders() {
        return new ArrayList<>(currentOrders);
    }
}