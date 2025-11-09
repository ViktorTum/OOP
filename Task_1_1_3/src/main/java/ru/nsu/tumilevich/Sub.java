package ru.nsu.tumilevich;

import java.util.Map;

record Sub(Expression left, Expression right) implements Expression {

	@Override
	public int evaluate(Map<String, Integer> context) {
		return left.evaluate(context) - right.evaluate(context);
	}

	@Override
	public Expression derivative(String var) {
		return (new Sub(left.derivative(var), right.derivative(var)));
	}

	@Override
	public String toString() {
		return "(" + left + " - " + right + ")";
	}
}
