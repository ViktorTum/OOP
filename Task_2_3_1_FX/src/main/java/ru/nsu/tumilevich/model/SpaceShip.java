package ru.nsu.tumilevich.model;

import ru.nsu.tumilevich.config.Config;

public class SpaceShip extends GameEntity {
    private static final long serialVersionUID = 1L;

    private int health;
    private final String playerName;
    private int score;
    private double speedMultiplier = 1.0;
    private double damageMultiplier = 1.0;
    private long speedBuffUntilNanos = 0;
    private long damageBuffUntilNanos = 0;
    private long lastShotNanos = 0;
    private final Vector2D velocity = new Vector2D(0, 0);

    public SpaceShip(Vector2D position, String playerName) {
        super(position, Config.PLAYER_RADIUS);
        this.health = Config.PLAYER_MAX_HEALTH;
        this.playerName = playerName;
        this.score = 0;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return Config.PLAYER_MAX_HEALTH; }
    public void setHealth(int health) { this.health = Math.min(Config.PLAYER_MAX_HEALTH, Math.max(0, health)); }
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public Vector2D getVelocity() { return velocity; }
    public void addScore(int points) { this.score += points; }
    public boolean isDead() { return health <= 0; }
    public void takeDamage(int damage) { setHealth(this.health - damage); }

    public void activateSpeedBuff(long nowNanos) {
        speedMultiplier = Config.PLAYER_SPEED_BUFF_MULTIPLIER;
        speedBuffUntilNanos = nowNanos + Config.PLAYER_BUFF_DURATION_NANOS;
    }

    public void activateDamageBuff(long nowNanos) {
        damageMultiplier = Config.PLAYER_DAMAGE_BUFF_MULTIPLIER;
        damageBuffUntilNanos = nowNanos + Config.PLAYER_BUFF_DURATION_NANOS;
    }

    public void updateBuffs(long nowNanos) {
        if (speedBuffUntilNanos > 0 && nowNanos >= speedBuffUntilNanos) {
            speedMultiplier = 1.0;
            speedBuffUntilNanos = 0;
        }
        if (damageBuffUntilNanos > 0 && nowNanos >= damageBuffUntilNanos) {
            damageMultiplier = 1.0;
            damageBuffUntilNanos = 0;
        }
    }

    public boolean canShoot(long nowNanos) {
        return nowNanos - lastShotNanos >= Config.SHOOT_COOLDOWN_NANOS;
    }

    public void markShot(long nowNanos) {
        lastShotNanos = nowNanos;
    }
}
