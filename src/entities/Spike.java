package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import main.Game;

public class Spike extends Entity {

    private static final int SPIKE_W = Game.TILES_SIZE;
    private static final int SPIKE_H = (int) (10 * Game.SCALE);
    private static final int DAMAGE = 2;

    // Cooldown so player isn't hit every single frame
    private int damageCooldown = 0;
    private static final int COOLDOWN_MAX = 60; // ~0.3s at 200 UPS

    public Spike(float x, float y) {
        super(x, y, SPIKE_W, SPIKE_H);
        // Hitbox sits at the bottom of the tile
        initHitbox(x, y + Game.TILES_SIZE - SPIKE_H, SPIKE_W, SPIKE_H);
    }

    public void update() {
        if (damageCooldown > 0)
            damageCooldown--;
    }

    public void draw(Graphics g, int xLvlOffset) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int drawX = (int) (hitbox.x - xLvlOffset);
        int baseY = (int) (hitbox.y + SPIKE_H);

        int numSpikes = 3;
        int spikeW = SPIKE_W / numSpikes;

        for (int i = 0; i < numSpikes; i++) {
            int sx = drawX + i * spikeW;
            int[] xPoints = {sx, sx + spikeW / 2, sx + spikeW};
            int[] yPoints = {baseY, baseY - SPIKE_H, baseY};
            g2d.setColor(new Color(160, 30, 30));
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setColor(new Color(100, 10, 10));
            g2d.drawPolygon(xPoints, yPoints, 3);
        }
    }

    public int checkPlayerCollision(Rectangle2D.Float playerHitbox) {
        if (damageCooldown <= 0 && hitbox.intersects(playerHitbox)) {
            damageCooldown = COOLDOWN_MAX;
            return DAMAGE;
        }
        return 0;
    }

    public void reset() {
        damageCooldown = 0;
    }

}