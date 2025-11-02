package ru.nsu.tumilevich;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилитарный класс для работы с математическими выражениями.
 */
public class ExpressionUtils {

    /**
     * Вычисляет значение выражения, используя контекст, заданный строкой.
     * Строка контекста должна быть в формате "var1=value1; var2=value2; ...".
     */
    public static int eval(Expression expression, String context) {
        String[] pairs = context.split("; ");
        Map<String, Integer> map = new HashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), Integer.parseInt(keyValue[1].trim()));
            }
        }

        // System.out.println(map); // Убираем отладочный вывод
        return expression.evaluate(map);
    }
}