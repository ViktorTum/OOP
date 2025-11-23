package ru.nsu.tumilevich;


public class HashTableExample {
    public static void main(String[] args) {
        // 1. Создание пустой хеш-таблицы
        HashTable<String, Number> hashTable = new HashTable<>();

        // 2. Добавление пар ключ-значение
        hashTable.put("one", 1);
        hashTable.put("two", 2);
        hashTable.put("three", 3);
        hashTable.put("four", 4.4);

        System.out.println("После добавления: " + hashTable);

        // 3. Поиск значения по ключу
        Number value = hashTable.get("two");
        System.out.println("Поиск ключа 'two': " + value);

        // 4. Проверка наличия ключа
        boolean containsOne = hashTable.contains("one");
        boolean containsFive = hashTable.contains("five");
        System.out.println("Содержит 'one': " + containsOne);
        System.out.println("Содержит 'five': " + containsFive);

        // 5. Обновление значения по ключу
        hashTable.update("one", 1.0);
        System.out.println("После обновления 'one': " + hashTable);

        // 6. Удаление пары ключ-значение
        boolean removed = hashTable.remove("three");
        System.out.println("Удален 'three': " + removed);
        System.out.println("После удаления: " + hashTable);

        // 7. Размер таблицы
        System.out.println("Размер таблицы: " + hashTable.size());

        // 8. Итерирование по элементам
        System.out.println("\nИтерирование по таблице:");
        for (java.util.Map.Entry<String, Number> entry : hashTable) {
            System.out.println("Ключ: " + entry.getKey() + ", Значение: " + entry.getValue());
        }

        // 9. Создание второй таблицы для сравнения
        HashTable<String, Number> hashTable2 = new HashTable<>();
        hashTable2.put("one", 1.0);
        hashTable2.put("two", 2);
        hashTable2.put("four", 4.4);

        // 10. Сравнение таблиц
        boolean areEqual = hashTable.equals(hashTable2);
        System.out.println("\nТаблицы равны: " + areEqual);

        // 11. Демонстрация ConcurrentModificationException
        System.out.println("\nДемонстрация ConcurrentModificationException:");
        try {
            for (java.util.Map.Entry<String, Number> entry : hashTable) {
                if (entry.getKey().equals("two")) {
                    hashTable.remove("two"); // Модификация во время итерации
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("Поймано исключение: " + e.getClass().getSimpleName());
        }

        // 12. Пример из задания
        HashTable<String, Number> testTable = new HashTable<>();
        testTable.put("one", 1);
        testTable.update("one", 1.0);
        System.out.println("\nТестовый пример:");
        System.out.println(testTable.get("one")); // Ожидаемый вывод: 1.0

        // 13. Демонстрация работы с коллизиями
        System.out.println("\nДемонстрация коллизий:");
        HashTable<Integer, String> collisionTable = new HashTable<>();

        // Добавляем элементы, которые могут иметь одинаковые хеши
        for (int i = 0; i < 10; i++) {
            collisionTable.put(i * 16, "Value_" + i); // Умножение на 16 для создания коллизий
        }

        System.out.println("Таблица с коллизиями: " + collisionTable);
        System.out.println("Размер: " + collisionTable.size());

        // Проверяем, что все элементы доступны
        for (int i = 0; i < 10; i++) {
            String val = collisionTable.get(i * 16);
            System.out.println("Ключ " + (i * 16) + ": " + val);
        }
    }
}