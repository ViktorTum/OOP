package ru.nsu.tumilevich.model;

public class SpaceShip extends GameEntity {
    private int health;
    private final int maxHealth = 100;
    private String playerName;
    private int score;

    public SpaceShip(Vector2D position, String playerName) {
        super(position, 20);
        this.health = maxHealth;
        this.playerName = playerName;
        this.score = 0;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = Math.min(maxHealth, Math.max(0, health)); }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }
    public boolean isDead() { return health <= 0; }
    public void takeDamage(int damage) { this.health -= damage; }
}
