package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Game;

public class SpikeManager {

    private ArrayList<Spike> spikes = new ArrayList<>();

    // Positions picked from verified air-above-solid spots, spread across
    // each level and not overlapping with coins.
    // Spikes sit on the floor (row 9) so the player must jump over them.
    private static final int[][][] SPIKE_POSITIONS = {
            // Level 1
            {
                    { 5, 9},
                    {20, 9},
                    {30, 9},
                    {40, 9},
            },
            // Level 2
            {
                    { 5, 9},
                    {16, 9},
                    {25, 9},
                    {43, 9},
            },
            // Level 3
            {
                    { 5, 9},
                    {16, 9},
                    {26, 9},
                    {32, 9},
            }
    };

    public SpikeManager() {
    }

    public void loadSpikes(int levelIndex) {
        spikes.clear();
        if (levelIndex >= SPIKE_POSITIONS.length)
            return;
        for (int[] pos : SPIKE_POSITIONS[levelIndex]) {
            float x = pos[0] * Game.TILES_SIZE;
            float y = pos[1] * Game.TILES_SIZE;
            spikes.add(new Spike(x, y));
        }
    }

    public int update(Rectangle2D.Float playerHitbox) {
        int totalDamage = 0;
        for (Spike s : spikes) {
            s.update();
            totalDamage += s.checkPlayerCollision(playerHitbox);
        }
        return totalDamage;
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (Spike s : spikes)
            s.draw(g, xLvlOffset);
    }

    public void resetAllSpikes() {
        for (Spike s : spikes)
            s.reset();
    }

}