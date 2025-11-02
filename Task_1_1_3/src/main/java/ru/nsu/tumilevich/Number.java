package ru.nsu.tumilevich;

import java.util.Map;

record Number(Integer value) implements Expression{

	@Override
	public int evaluate(Map<String, Integer> context) {
		return value;
	}

	@Override
	public Expression derivative(String var) {
		return (new Number(0));
	}

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
