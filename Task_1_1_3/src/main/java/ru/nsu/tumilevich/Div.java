package ru.nsu.tumilevich;

import java.util.Map;

record Div(Expression left, Expression right) implements Expression {

	@Override
	public int evaluate(Map<String, Integer> context) {
		int rightVal = right.evaluate(context);
		if (rightVal == 0) {
			throw new ArithmeticException("Division by zero");
		}
		return left.evaluate(context) / rightVal;
	}

	@Override
	public Expression derivative(String var) {
		return new Div(
				new Sub(
						new Mul(left.derivative(var), right),
						new Mul(left, right.derivative(var))
				),
				new Mul(right, right)
		);
	}

	@Override
	public String toString() {
		return "(" + left + " / " + right + ")";
	}
}