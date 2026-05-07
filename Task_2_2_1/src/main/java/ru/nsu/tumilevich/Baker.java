package ru.nsu.tumilevich;

/**
 * Представляет пекаря (worker-поток), который берет заказы из очереди заказов,
 * готовит их заданное время и помещает готовые пиццы на склад.
 */
public class Baker implements Runnable {
    private final int id;
    private final int cookingTime;
    private final BlockingQueue<Order> orderQueue;
    private final BlockingQueue<Order> stock;

    private Order currentOrder = null;

    /**
     * Создает нового пекаря.
     *
     * @param id          уникальный идентификатор пекаря
     * @param cookingTime время приготовления одной пиццы (в миллисекундах)
     * @param orderQueue  входящая очередь новых заказов
     * @param stock       склад (очередь) для готовых пицц
     */
    public Baker(int id, int cookingTime, BlockingQueue<Order> orderQueue, BlockingQueue<Order> stock) {
        this.id = id;
        this.cookingTime = cookingTime;
        this.orderQueue = orderQueue;
        this.stock = stock;
    }

    @Override
    public void run() {
        try {
            while (true) {
                currentOrder = orderQueue.take();
                if (currentOrder == null) break;

                currentOrder.setStatus(Order.OrderStatus.COOKING);
                Thread.sleep(cookingTime);
                currentOrder.setStatus(Order.OrderStatus.COOKED);

                stock.put(currentOrder);
                currentOrder.setStatus(Order.OrderStatus.IN_STOCK);

                currentOrder = null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}