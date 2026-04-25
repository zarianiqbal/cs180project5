package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Game;

public class CoinManager {

    private ArrayList<Coin> coins = new ArrayList<>();

    // Coin positions per level [levelIndex][coin][tileX, tileY]
    // Placed on row 10 which sits just above solid ground (row 11)
    private static final int[][][] COIN_POSITIONS = {
            // Level 1
            {
                    {3,  9},
                    {5,  9},
                    {15, 9},
                    {20, 9},
                    {25, 9},
                    {30, 9},
            },
            // Level 2
            {
                    {3,  9},
                    {7,  9},
                    {14, 9},
                    {22, 9},
                    {28, 9},
                    {35, 9},
            },
            // Level 3
            {
                    {3,  9},
                    {6,  9},
                    {12, 9},
                    {18, 9},
                    {24, 9},
                    {30, 9},
            }
    };

    public CoinManager() {
    }

    public void loadCoins(int levelIndex) {
        coins.clear();

        if (levelIndex >= COIN_POSITIONS.length)
            return;

        for (int[] pos : COIN_POSITIONS[levelIndex]) {
            float x = pos[0] * Game.TILES_SIZE;
            float y = pos[1] * Game.TILES_SIZE;
            coins.add(new Coin(x, y));
        }
    }

    public void update(Rectangle2D.Float playerHitbox) {
        for (Coin c : coins)
            if (!c.isCollected())
                c.checkPlayerCollision(playerHitbox);
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (Coin c : coins)
            if (!c.isCollected())
                c.draw(g, xLvlOffset);
    }

    public void resetAllCoins() {
        for (Coin c : coins)
            c.reset();
    }

}