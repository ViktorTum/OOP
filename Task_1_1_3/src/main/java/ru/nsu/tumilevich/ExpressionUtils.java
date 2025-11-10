package ru.nsu.tumilevich;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилитарный класс для работы с математическими выражениями.
 */
public class ExpressionUtils {
    /**
     * Парсит строку контекста в Map.
     * Строка контекста должна быть в формате "var1=value1; var2=value2; ...".
     */
    public static Map<String, Integer> parseContext(String context) {
        Map<String, Integer> map = new HashMap<>();

        // Если контекст пустой, возвращаем пустую карту
        if (context == null || context.trim().isEmpty()) {
            return map;
        }

        String[] pairs = context.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), Integer.parseInt(keyValue[1].trim()));
            }
        }

        return map;
    }

    /**
     * Вычисляет значение выражения, используя контекст, заданный строкой.
     * Строка контекста должна быть в формате "var1=value1; var2=value2; ...".
     */
    public static int eval(Expression expression, String context) {
        Map<String, Integer> map = parseContext(context);
        return expression.evaluate(map);
    }
}
