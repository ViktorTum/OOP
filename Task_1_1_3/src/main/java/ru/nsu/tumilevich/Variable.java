package ru.nsu.tumilevich;

import java.util.Map;
import java.util.Objects;

record Variable(String name) implements Expression{
	@Override
	public int evaluate(Map<String, Integer> context) {
		if (context.containsKey(name)){
			return context.get(name);
		}
		return 0;
	}

	@Override
	public Expression derivative(String var) {
		if (Objects.equals(var, name)){
			return (new Number(1));
		}
		else {
			return (new Number(0));
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
