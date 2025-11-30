package ru.nsu.tumilevich;

import java.util.*;

public class HashTable<K, V> implements Iterable<Map.Entry<K, V>> {
    private static final int DEFAULT_CAPACITY = 16;     //размер таблицы
    private static final float LOAD_FACTOR = 0.75f;     //увеличение таблицы, при заполненности более LOAD_FACTOR

    private Node<K, V>[] table;
    private int size;
    private int modCount;

    /**
    * создание таблицы
    */

    public HashTable() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        size = 0;
        modCount = 0;
    }


    //функция добавления
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        int index = getIndex(key);
        Node<K, V> current = table[index];

        // Поиск существующего ключа
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // Добавление нового узла
        table[index] = new Node<>(key, value, table[index]);
        size++;
        modCount++;

        if (size > table.length * LOAD_FACTOR) {
            resize();
        }
    }

    //поиск
    public V get(K key) {
        int index = getIndex(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    //удаление
    public boolean remove(K key) {
        int index = getIndex(key);
        Node<K, V> current = table[index];
        Node<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                modCount++;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    //обновление
    public void update(K key, V value) {
        //используем put для одинаковой логики обновления
        put(key, value);
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        HashTable<?, ?> other = (HashTable<?, ?>) obj;
        if (this.size != other.size) return false;

        //сравниваем через итерацию по обеим таблицам
        Iterator<?> otherIterator = other.iterator();
        Iterator<Map.Entry<K, V>> thisIterator = this.iterator();

        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            Map.Entry<?, ?> thisEntry = thisIterator.next();
            Map.Entry<?, ?> otherEntry = (Map.Entry<?, ?>) otherIterator.next();

            if (!Objects.equals(thisEntry.getKey(), otherEntry.getKey()) ||
                    !Objects.equals(thisEntry.getValue(), otherEntry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<K, V> entry : this) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    private int getIndex(K key) {
        return (key.hashCode() & 0x7FFFFFFF) % table.length;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[oldTable.length * 2];
        modCount++;

        for (Node<K, V> node : oldTable) {
            while (node != null) {
                Node<K, V> next = node.next;
                int newIndex = getIndex(node.key);

                node.next = table[newIndex];
                table[newIndex] = node;

                node = next;
            }
        }
    }

    private static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() { return key; }

        @Override
        public V getValue() { return value; }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    private class HashTableIterator implements Iterator<Map.Entry<K, V>> {
        private int currentIndex = 0;
        private Node<K, V> currentNode = null;
        private final int expectedModCount;

        HashTableIterator() {
            expectedModCount = modCount;
            findNext();
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) throw new NoSuchElementException();

            Node<K, V> result = currentNode;
            currentNode = currentNode.next;
            if (currentNode == null) {
                currentIndex++;
                findNext();
            }

            return result;
        }

        private void findNext() {
            while (currentIndex < table.length) {
                currentNode = table[currentIndex];
                if (currentNode != null) break;
                currentIndex++;
            }
        }
    }
}
