package ru.nsu.tumilevich;

import java.util.Map;

/*
	Для создания нового операции нужно реализовать функци evaluate derivative toString в новом классе функции
	а так же класс должен implements Expression
 */
interface Expression{
	/*
		evaluate - реализация функции
		derivative - производная этой функции
	 */

	int evaluate(Map<String, Integer> context);
	Expression derivative(String var);

}