package ru.nsu.tumilevich.model;

public class Bullet extends GameEntity {
    private static final long serialVersionUID = 1L;

    private final String ownerId;
    private final Vector2D velocity;
    private final int damage;

    public Bullet(Vector2D position, Vector2D velocity, String ownerId, int damage) {
        super(position, 5);
        this.velocity = velocity;
        this.ownerId = ownerId;
        this.damage = damage;
    }

    public String getOwnerId() { return ownerId; }
    public Vector2D getVelocity() { return velocity; }
    public int getDamage() { return damage; }

    public void update(double deltaTime) {
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
    }
}
