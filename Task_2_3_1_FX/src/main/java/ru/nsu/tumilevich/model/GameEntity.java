package ru.nsu.tumilevich.model;

import java.io.Serializable;
import java.util.UUID;

public abstract class GameEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    protected Vector2D position;
    protected double rotation;
    protected double radius;

    public GameEntity(Vector2D position, double radius) {
        this.id = UUID.randomUUID().toString();
        this.position = position;
        this.radius = radius;
    }

    public String getId() { return id; }
    public Vector2D getPosition() { return position; }
    public double getRotation() { return rotation; }
    public double getRadius() { return radius; }

    public void setPosition(Vector2D position) { this.position = position; }
    public void setRotation(double rotation) { this.rotation = rotation; }

    public boolean collidesWith(GameEntity other) {
        return this.position.distance(other.position) <= (this.radius + other.radius);
    }
}
