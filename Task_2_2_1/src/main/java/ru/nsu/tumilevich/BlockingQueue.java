package ru.nsu.tumilevich;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Потокобезопасная блокирующая очередь с ограниченной вместимостью.
 * Используется для обмена заказами между потоками (Main -> Baker -> Courier).
 * Поддерживает механизм "закрытия", предотвращающий бесконечное ожидание при остановке системы.
 *
 * @param <T> тип элементов, хранящихся в очереди (в данном проекте - Order)
 */

public class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private boolean isClosed = false;

    /**
     * Создает новую блокирующую очередь с заданной максимальной вместимостью.
     *
     * @param capacity максимальное количество элементов в очереди
     */

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }


    /**
     * Помещает элемент в очередь. Если очередь заполнена, вызывающий поток блокируется
     * до появления свободного места или до закрытия очереди.
     *
     * @param item элемент для добавления
     * @throws InterruptedException если поток был прерван во время ожидания
     * или если очередь закрыта и заполнена
     */
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity && !isClosed) {
            wait();
        }
        if (isClosed && queue.size() == capacity) {
            throw new InterruptedException("Queue is closed and full");
        }
        queue.add(item);
        notifyAll();
    }

    /**
     * Извлекает и удаляет элемент из начала очереди. Если очередь пуста,
     * вызывающий поток блокируется до появления элемента или закрытия очереди.
     *
     * @return элемент из очереди или {@code null}, если очередь пуста и закрыта
     * @throws InterruptedException если поток был прерван во время ожидания
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && !isClosed) {
            wait();
        }
        if (queue.isEmpty() && isClosed) return null;
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized T poll() {
        if (queue.isEmpty()) return null;
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized void close() {
        isClosed = true;
        notifyAll();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Возвращает копию всех элементов, находящихся в очереди в данный момент.
     * Используется для сохранения состояния при остановке системы.
     *
     * @return новая очередь, содержащая все текущие элементы
     */
    public synchronized Queue<T> getAll() {
        return new LinkedList<>(queue);
    }
}