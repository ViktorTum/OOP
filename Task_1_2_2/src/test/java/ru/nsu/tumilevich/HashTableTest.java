package ru.nsu.tumilevich;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class HashTableTest {

	@Test
	public void testConstructor() {
		// Тест 1: Создание пустой таблицы
		HashTable<String, Integer> table = new HashTable<>();
		assertNotNull(table);
		assertEquals(0, table.size());
	}

	@Test
	public void testPutAndGet() {
		// Тест 2: Добавление и получение элементов
		HashTable<String, Integer> table = new HashTable<>();

		table.put("one", 1);
		table.put("two", 2);
		table.put("three", 3);

		assertEquals(1, table.get("one"));
		assertEquals(2, table.get("two"));
		assertEquals(3, table.get("three"));
		assertEquals(3, table.size());
	}

	@Test
	public void testPutOverride() {
		// Тест 3: Перезапись существующего ключа
		HashTable<String, Integer> table = new HashTable<>();

		table.put("key", 100);
		assertEquals(100, table.get("key"));

		table.put("key", 200); // Перезапись
		assertEquals(200, table.get("key"));
		assertEquals(1, table.size()); // Размер не должен измениться
	}

	@Test
	public void testGetNonExistent() {
		// Тест 4: Получение несуществующего ключа
		HashTable<String, Integer> table = new HashTable<>();
		table.put("exists", 1);

		assertNull(table.get("nonexistent"));
	}

	@Test
	public void testRemove() {
		// Тест 5: Удаление элементов
		HashTable<String, Integer> table = new HashTable<>();
		table.put("one", 1);
		table.put("two", 2);
		table.put("three", 3);

		assertTrue(table.remove("two"));
		assertNull(table.get("two"));
		assertEquals(2, table.size());

		assertFalse(table.remove("nonexistent")); // Удаление несуществующего
		assertEquals(2, table.size());
	}

	@Test
	public void testUpdate() {
		// Тест 6: Обновление значений
		HashTable<String, Number> table = new HashTable<>();
		table.put("number", 1);
		table.update("number", 1.5);

		assertEquals(1.5, table.get("number"));

		// Обновление несуществующего ключа (должно создать новый)
		table.update("newKey", 100);
		assertEquals(100, table.get("newKey"));
	}

	@Test
	public void testContains() {
		// Тест 7: Проверка наличия ключей
		HashTable<String, Integer> table = new HashTable<>();
		table.put("present", 1);

		assertTrue(table.contains("present"));
		assertFalse(table.contains("absent"));
	}

	@Test
	public void testSize() {
		// Тест 8: Проверка размера
		HashTable<String, Integer> table = new HashTable<>();
		assertEquals(0, table.size());

		table.put("a", 1);
		assertEquals(1, table.size());

		table.put("b", 2);
		assertEquals(2, table.size());

		table.remove("a");
		assertEquals(1, table.size());

		table.remove("nonexistent"); // Не должен изменить размер
		assertEquals(1, table.size());
	}

	@Test
	public void testIterator() {
		// Тест 9: Проверка итератора
		HashTable<String, Integer> table = new HashTable<>();
		table.put("a", 1);
		table.put("b", 2);
		table.put("c", 3);

		Set<String> keys = new HashSet<>();
		Set<Integer> values = new HashSet<>();

		for (Map.Entry<String, Integer> entry : table) {
			keys.add(entry.getKey());
			values.add(entry.getValue());
		}

		assertEquals(3, keys.size());
		assertTrue(keys.contains("a"));
		assertTrue(keys.contains("b"));
		assertTrue(keys.contains("c"));

		assertEquals(3, values.size());
		assertTrue(values.contains(1));
		assertTrue(values.contains(2));
		assertTrue(values.contains(3));
	}

	@Test
	public void testIteratorEmptyTable() {
		// Тест 10: Итератор пустой таблицы
		HashTable<String, Integer> table = new HashTable<>();

		Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
		assertFalse(iterator.hasNext());

		assertThrows(NoSuchElementException.class, iterator::next);
	}

	@Test
	public void testConcurrentModificationException() {
		// Тест 11: Проверка ConcurrentModificationException
		HashTable<String, Integer> table = new HashTable<>();
		table.put("a", 1);
		table.put("b", 2);
		table.put("c", 3);

		Iterator<Map.Entry<String, Integer>> iterator = table.iterator();

		// Модификация во время итерации
		table.put("d", 4);

		assertThrows(ConcurrentModificationException.class, iterator::next);
	}

	@Test
	public void testEquals() {
		// Тест 12: Проверка равенства таблиц
		HashTable<String, Integer> table1 = new HashTable<>();
		HashTable<String, Integer> table2 = new HashTable<>();

		// Пустые таблицы равны
		assertEquals(table1, table2);

		// Таблицы с одинаковыми элементами равны
		table1.put("a", 1);
		table1.put("b", 2);

		table2.put("a", 1);
		table2.put("b", 2);

		assertEquals(table1, table2);

		// Таблицы с разными элементами не равны
		table2.put("c", 3);
		assertNotEquals(table1, table2);

		// Таблица не равна null
		assertNotEquals(null, table1);

		// Таблица не равна объекту другого класса
		assertNotEquals("string", table1);
	}

	@Test
	public void testToString() {
		// Тест 13: Строковое представление
		HashTable<String, Integer> table = new HashTable<>();

		// Пустая таблица
		assertEquals("{}", table.toString());

		// Таблица с одним элементом
		table.put("one", 1);
		assertEquals("{one=1}", table.toString());

		// Таблица с несколькими элементами
		table.put("two", 2);
		String result = table.toString();
		assertTrue(result.contains("one=1"));
		assertTrue(result.contains("two=2"));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test
	public void testCollisions() {
		// Тест 14: Обработка коллизий
		HashTable<Integer, String> table = new HashTable<>();

		// Создаем коллизии, используя ключи с одинаковым хешем
		// В маленькой таблице несколько ключей могут попасть в одну корзину
		for (int i = 0; i < 20; i++) {
			table.put(i, "value" + i);
		}

		// Проверяем, что все элементы доступны
		for (int i = 0; i < 20; i++) {
			assertEquals("value" + i, table.get(i));
		}

		assertEquals(20, table.size());
	}

	@Test
	public void testResize() {
		// Тест 15: Автоматическое увеличение размера
		HashTable<Integer, String> table = new HashTable<>();

		// Добавляем больше элементов, чем начальная емкость * load factor
		// DEFAULT_CAPACITY = 16, LOAD_FACTOR = 0.75 -> resize при 12 элементах
		for (int i = 0; i < 20; i++) {
			table.put(i, "value" + i);
		}

		// Проверяем, что все элементы сохранились после resize
		for (int i = 0; i < 20; i++) {
			assertEquals("value" + i, table.get(i));
		}

		assertEquals(20, table.size());
	}

	@Test
	public void testNullKey() {
		// Тест 16: Обработка null ключа
		HashTable<String, Integer> table = new HashTable<>();

		assertThrows(IllegalArgumentException.class, () -> {
			table.put(null, 1);
		});
	}

	@Test
	public void testComplexTypes() {
		// Тест 17: Работа со сложными типами
		HashTable<List<String>, Map<Integer, String>> table = new HashTable<>();

		List<String> key1 = Arrays.asList("a", "b", "c");
		Map<Integer, String> value1 = new HashMap<>();
		value1.put(1, "one");
		value1.put(2, "two");

		List<String> key2 = Arrays.asList("x", "y");
		Map<Integer, String> value2 = new HashMap<>();
		value2.put(3, "three");

		table.put(key1, value1);
		table.put(key2, value2);

		assertEquals(value1, table.get(key1));
		assertEquals(value2, table.get(key2));
		assertEquals(2, table.size());
	}

	@Test
	public void testPerformance() {
		// Тест 18: Проверка производительности (большое количество элементов)
		HashTable<Integer, String> table = new HashTable<>();
		int count = 1000;

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < count; i++) {
			table.put(i, "value" + i);
		}

		long putTime = System.currentTimeMillis() - startTime;

		startTime = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			assertEquals("value" + i, table.get(i));
		}
		long getTime = System.currentTimeMillis() - startTime;

		// Проверяем, что операции выполняются за разумное время
		assertTrue(putTime < 1000, "Put operations took too long: " + putTime + "ms");
		assertTrue(getTime < 1000, "Get operations took too long: " + getTime + "ms");

		assertEquals(count, table.size());
	}

	@Test
	public void testIntegration() {
		// Тест 19: Интеграционный тест - комбинация операций
		HashTable<String, Integer> table = new HashTable<>();

		// Добавление
		table.put("start", 0);
		assertEquals(1, table.size());

		// Обновление
		table.update("start", 100);
		assertEquals(100, table.get("start"));

		// Проверка наличия
		assertTrue(table.contains("start"));

		// Добавление нескольких элементов
		for (int i = 1; i <= 5; i++) {
			table.put("key" + i, i);
		}
		assertEquals(6, table.size());

		// Удаление
		assertTrue(table.remove("start"));
		assertEquals(5, table.size());

		// Итерация
		int count = 0;
		for (Map.Entry<String, Integer> entry : table) {
			assertNotNull(entry.getKey());
			assertNotNull(entry.getValue());
			count++;
		}
		assertEquals(5, count);

		// Строковое представление
		assertNotNull(table.toString());
	}

	@Test
	public void testExampleFromTask() {
		// Тест 20: Пример из задания
		HashTable<String, Number> hashTable = new HashTable<>();
		hashTable.put("one", 1);
		hashTable.update("one", 1.0);
		assertEquals(1.0, hashTable.get("one"));
	}
}