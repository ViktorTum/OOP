package ru.nsu.tumilevich;

import java.util.Map;

interface Expression{
	//Вычисляет значение выражения в заданном контексте.

	int evaluate(Map<String, Integer> context);
	//Вычисляет производную выражения по заданной переменной.
	Expression derivative(String var);
	String toString();


}