package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import main.Game;

public class Coin extends Entity {

    private boolean collected = false;

    // Coin size in pixels (scaled)
    private static final int COIN_SIZE = (int) (12 * Game.SCALE);

    public Coin(float x, float y) {
        super(x, y, COIN_SIZE, COIN_SIZE);
        initHitbox(x, y, COIN_SIZE, COIN_SIZE);
    }

    public void update() {
        // Nothing to update — coin is static until collected
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (collected)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int drawX = (int) (hitbox.x - xLvlOffset);
        int drawY = (int) hitbox.y;

        // Outer coin body
        g2d.setColor(new Color(255, 200, 0));
        g2d.fillOval(drawX, drawY, COIN_SIZE, COIN_SIZE);

        // Dark edge ring
        g2d.setColor(new Color(180, 130, 0));
        g2d.drawOval(drawX, drawY, COIN_SIZE, COIN_SIZE);

        // Small shine highlight
        g2d.setColor(new Color(255, 240, 150, 200));
        g2d.fillOval(drawX + COIN_SIZE / 4, drawY + COIN_SIZE / 5, COIN_SIZE / 4, COIN_SIZE / 4);
    }

    public void checkPlayerCollision(Rectangle2D.Float playerHitbox) {
        if (!collected && hitbox.intersects(playerHitbox))
            collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public void reset() {
        collected = false;
    }

}