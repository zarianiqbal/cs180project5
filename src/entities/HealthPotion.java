package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import main.Game;

public class HealthPotion extends Entity {

    private static final int POTION_SIZE = (int) (12 * Game.SCALE);
    private static final int HEAL_AMOUNT = 3;
    private boolean collected = false;

    public HealthPotion(float x, float y) {
        super(x, y, POTION_SIZE, POTION_SIZE);
        initHitbox(x, y, POTION_SIZE, POTION_SIZE);
    }

    public void update() {
        // Static — nothing to update
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (collected)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int drawX = (int) (hitbox.x - xLvlOffset);
        int drawY = (int) hitbox.y;
        int s = POTION_SIZE;

        // Flask body (rounded bottom)
        g2d.setColor(new Color(200, 50, 50, 220));
        g2d.fillOval(drawX, drawY + s / 3, s, s * 2 / 3);

        // Flask neck
        g2d.setColor(new Color(200, 50, 50, 220));
        g2d.fillRect(drawX + s / 3, drawY, s / 3, s / 3);

        // Flask neck outline
        g2d.setColor(new Color(120, 20, 20));
        g2d.drawRect(drawX + s / 3, drawY, s / 3, s / 3);
        g2d.drawOval(drawX, drawY + s / 3, s, s * 2 / 3);

        // Shine on body
        g2d.setColor(new Color(255, 160, 160, 160));
        g2d.fillOval(drawX + s / 5, drawY + s / 2, s / 4, s / 4);

        // Cork / stopper
        g2d.setColor(new Color(180, 140, 80));
        g2d.fillRect(drawX + s / 3, drawY - s / 8, s / 3, s / 8);
    }

    public int checkPlayerCollision(Rectangle2D.Float playerHitbox) {
        if (!collected && hitbox.intersects(playerHitbox)) {
            collected = true;
            return HEAL_AMOUNT;
        }
        return 0;
    }

    public boolean isCollected() {
        return collected;
    }

    public void reset() {
        collected = false;
    }

}