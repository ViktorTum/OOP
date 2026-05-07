package ru.nsu.tumilevich;

import java.io.Serializable;

/**
 * Представляет сущность заказа в пиццерии.
 * Хранит уникальный идентификатор и текущий статус приготовления/доставки.
 * Реализует {@link Serializable} для возможности сохранения незавершенных заказов в файл.
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private OrderStatus status;

    public Order(int id) {
        this.id = id;
        this.status = OrderStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        System.out.println("[" + id + "] [" + status + "]");
    }

    public enum OrderStatus {
        PENDING,
        COOKING,
        COOKED,
        IN_STOCK,
        DELIVERING,
        DELIVERED
    }
}