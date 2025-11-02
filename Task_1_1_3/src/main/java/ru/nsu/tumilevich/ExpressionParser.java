package ru.nsu.tumilevich;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {
	private String input;
	private int pos;

	public Expression parse(String input) {
		this.input = input.replaceAll("\\s+", ""); // Удаляем все пробелы
		this.pos = 0;
		return parseExpression();
	}

	private Expression parseExpression() {
		if (peek() == '(') {
			return parseBinaryOperation();
		} else if (Character.isDigit(peek()) || peek() == '-') {
			return parseNumber();
		} else {
			return parseVariable();
		}
	}

	private Expression parseBinaryOperation() {
		expect('(');
		Expression left = parseExpression();

		String operator = parseOperator();

		Expression right = parseExpression();
		expect(')');

		switch (operator) {
			case "+": return new Add(left, right);
			case "-": return new Sub(left, right);
			case "*": return new Mul(left, right);
			case "/": return new Div(left, right);
			default: throw new IllegalArgumentException("Unknown operator: " + operator);
		}
	}

	private String parseOperator() {
		List<Character> operators = List.of('+', '-', '*', '/');
		for (char op : operators) {
			if (peek() == op) {
				pos++;
				return String.valueOf(op);
			}
		}
		throw new IllegalArgumentException("Expected operator at position " + pos);
	}

	private Expression parseNumber() {
		StringBuilder sb = new StringBuilder();

		// Обрабатываем отрицательные числа
		if (peek() == '-') {
			sb.append(consume());
		}

		while (pos < input.length() && Character.isDigit(peek())) {
			sb.append(consume());
		}

		return new Number(Integer.parseInt(sb.toString()));
	}

	private Expression parseVariable() {
		StringBuilder sb = new StringBuilder();
		while (pos < input.length() && Character.isLetterOrDigit(peek())) {
			sb.append(consume());
		}
		return new Variable(sb.toString());
	}

	private char peek() {
		if (pos >= input.length()) {
			throw new IllegalArgumentException("Unexpected end of input");
		}
		return input.charAt(pos);
	}

	private char consume() {
		if (pos >= input.length()) {
			throw new IllegalArgumentException("Unexpected end of input");
		}
		return input.charAt(pos++);
	}

	private void expect(char expected) {
		if (peek() != expected) {
			throw new IllegalArgumentException("Expected '" + expected + "' at position " + pos);
		}
		pos++;
	}
}