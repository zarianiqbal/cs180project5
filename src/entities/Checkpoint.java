package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import main.Game;

public class Checkpoint extends Entity {

    private boolean activated = false;

    private static final int FLAG_W = (int) (6 * Game.SCALE);
    private static final int FLAG_H = (int) (40 * Game.SCALE);

    public Checkpoint(float x, float y) {
        super(x, y, FLAG_W, FLAG_H);
        initHitbox(x + FLAG_W / 2, y - FLAG_H + Game.TILES_SIZE, FLAG_W, FLAG_H);
    }

    public void update() {
    }

    public void draw(Graphics g, int xLvlOffset) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int poleX = (int) (hitbox.x - xLvlOffset) + FLAG_W / 2;
        int poleTop = (int) hitbox.y;
        int poleBottom = (int) (hitbox.y + FLAG_H);

        // Pole
        g2d.setColor(new Color(180, 180, 180));
        g2d.fillRect(poleX - 2, poleTop, 4, FLAG_H);
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawRect(poleX - 2, poleTop, 4, FLAG_H);

        // Ball on top
        g2d.setColor(new Color(220, 220, 50));
        g2d.fillOval(poleX - 5, poleTop - 5, 10, 10);
        g2d.setColor(new Color(150, 150, 20));
        g2d.drawOval(poleX - 5, poleTop - 5, 10, 10);

        // Flag — white when not activated, bright green when activated
        int flagW = (int) (16 * Game.SCALE);
        int flagH = (int) (10 * Game.SCALE);
        Color flagColor = activated ? new Color(80, 220, 60)  : new Color(255, 255, 255);
        Color flagEdge  = activated ? new Color(40, 160, 30)  : new Color(180, 180, 180);

        g2d.setColor(flagColor);
        g2d.fillRect(poleX + 2, poleTop, flagW, flagH);
        g2d.setColor(flagEdge);
        g2d.drawRect(poleX + 2, poleTop, flagW, flagH);

        // Base
        g2d.setColor(new Color(100, 80, 50));
        g2d.fillRect(poleX - 8, poleBottom - 4, 16, 4);
    }

    public boolean checkPlayerCollision(Rectangle2D.Float playerHitbox) {
        if (!activated && hitbox.intersects(playerHitbox)) {
            activated = true;
            return true;
        }
        return false;
    }

    // Force-activates the flag without requiring player collision —
    // used when respawning at a checkpoint from a previous level.
    public void forceActivate() {
        activated = true;
    }

    public boolean isActivated() {
        return activated;
    }

    public float getSpawnX() {
        return hitbox.x;
    }

    public float getSpawnY() {
        return hitbox.y + FLAG_H - Game.TILES_SIZE;
    }

    public void reset() {
        activated = false;
    }

}