package ru.nsu.tumilevich.model;

public class Bullet extends GameEntity {
    private final String ownerId;
    private final Vector2D velocity;

    public Bullet(Vector2D position, Vector2D velocity, String ownerId) {
        super(position, 5);
        this.velocity = velocity;
        this.ownerId = ownerId;
    }

    public String getOwnerId() { return ownerId; }
    public Vector2D getVelocity() { return velocity; }
    
    public void update() {
        position.add(velocity);
    }
}
