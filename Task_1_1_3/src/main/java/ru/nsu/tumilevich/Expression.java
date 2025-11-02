package ru.nsu.tumilevich;
import java.util.HashMap;
import java.util.Map;


interface Expression{
	int evaluate(Map<String, Integer> context);
	Expression derivative(String var);
	String toString();

	default int eval(String context) {
		String[] pairs = context.split("; ");
		Map<String, Integer> map = new HashMap<>();
		for (String pair : pairs) {
			String[] keyValue = pair.split("="); // Разделяем каждую пару на ключ и значение
			if (keyValue.length == 2) {
				map.put(keyValue[0].trim(), Integer.parseInt(keyValue[1].trim())); // Добавляем в карту
			}
		}

		System.out.println(map);
		return this.evaluate(map);
	}

	default void print() {
		System.out.println(this.toString());
	}
}
