package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Game;

public class HealthPotionManager {

    private ArrayList<HealthPotion> potions = new ArrayList<>();

    // Placed on raised platforms so the player has to jump to reach them —
    // makes them a meaningful reward rather than free health.
    // Verified as air-above-solid spots not used by coins or spikes.
    private static final int[][][] POTION_POSITIONS = {
            // Level 1
            {
                    { 9, 7},   // raised middle platform
            },
            // Level 2
            {
                    { 9, 7},   // raised left platform
                    {34, 7},   // raised right bridge
            },
            // Level 3
            {
                    { 9, 7},   // raised left platform
                    {22, 7},   // raised right platform
            }
    };

    public HealthPotionManager() {
    }

    public void loadPotions(int levelIndex) {
        potions.clear();
        if (levelIndex >= POTION_POSITIONS.length)
            return;
        for (int[] pos : POTION_POSITIONS[levelIndex]) {
            float x = pos[0] * Game.TILES_SIZE;
            float y = pos[1] * Game.TILES_SIZE;
            potions.add(new HealthPotion(x, y));
        }
    }

    public int update(Rectangle2D.Float playerHitbox) {
        int totalHeal = 0;
        for (HealthPotion p : potions)
            if (!p.isCollected())
                totalHeal += p.checkPlayerCollision(playerHitbox);
        return totalHeal;
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (HealthPotion p : potions)
            if (!p.isCollected())
                p.draw(g, xLvlOffset);
    }

    public void resetAllPotions() {
        for (HealthPotion p : potions)
            p.reset();
    }

}