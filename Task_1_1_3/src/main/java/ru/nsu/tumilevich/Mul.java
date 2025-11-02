package ru.nsu.tumilevich;

import java.util.Map;

record Mul(Expression left, Expression right) implements Expression {

	@Override
	public int evaluate(Map<String, Integer> context) {
		return left.evaluate(context) * right.evaluate(context);
	}

	@Override
	public Expression derivative(String var) {
		return (new Add((new Mul(left.derivative(var), right)), new Mul(left, right.derivative(var))));
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " * " + right.toString() + ")";
	}
}