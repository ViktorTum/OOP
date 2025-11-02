package ru.nsu.tumilevich;



public class MainTask3 {
	public static void main(String[] args) {
		// Пример использования
		Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));

		Expression de = e.derivative("x");
		System.out.println("Выражение: " + e.toString());
		System.out.println("Производная по x: " + de.toString());

		int result = ExpressionUtils.eval(e, "x = 10; y = 13");
		System.out.println("Результат при x = 10; y = 13: " + result);



		System.out.println("\n--- Парсинг из строки ---");

		ExpressionParser parser = new ExpressionParser();

		// Примеры выражений для парсинга
		String[] expressions = {
				"(3 + 2)",
				"(x * 5)",
				"((2 * x) + 3)",
				"(((x * x) + (2 * x)) + 1)",
				"((x + 1) / (x - 1))"
		};

		for (String exprStr : expressions) {
			try {
				System.out.println("\nПарсинг: " + exprStr);
				Expression parsedExpr = parser.parse(exprStr);
				System.out.println("Выражение: " + parsedExpr.toString());


				Expression derivative = parsedExpr.derivative("x");
				System.out.print("Производная: ");
				System.out.println("Производная: " + derivative.toString());


				int parsedResult = ExpressionUtils.eval(parsedExpr, "x = 2");
				System.out.println("Результат при x=2: " + parsedResult);

			} catch (Exception ex) {
				System.out.println("Ошибка парсинга: " + ex.getMessage());
			}
		}

		// Пример чтения из аргументов командной строки
		if (args.length > 0) {
			try {
				System.out.println("\n--- Из командной строки ---");
				Expression cmdExpr = parser.parse(args[0]);
				System.out.println("Выражение: " + cmdExpr.toString());

				Expression cmdDerivative = cmdExpr.derivative("x");
				System.out.println("Производная: " + cmdDerivative.toString());

			} catch (Exception ex) {
				System.out.println("Ошибка парсинга аргумента: " + ex.getMessage());
			}
		}
	}
}