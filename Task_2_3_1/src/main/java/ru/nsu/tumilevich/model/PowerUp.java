package ru.nsu.tumilevich.model;

public class PowerUp extends GameEntity {
    public enum Type { HEALTH, SPEED, DAMAGE }
    private final Type type;

    public PowerUp(Vector2D position, Type type) {
        super(position, 15);
        this.type = type;
    }

    public Type getType() { return type; }
}
