package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Game;

public class CoinManager {

    private ArrayList<Coin> coins = new ArrayList<>();

    // Each entry: {tileCol, tileRow} — verified as air above solid ground
    // from the actual level pixel data.
    //
    // Level 1 (50 tiles wide):
    //   Main floor is row 10, so coins sit at row 9.
    //   Raised platform cols 8-11 sits at row 8, so coins there at row 7.
    //
    // Level 2 (50 tiles wide):
    //   Same floor layout as level 1 plus a second raised bridge cols 29-37 at row 9.
    //   Coins on that bridge sit at row 7.
    //
    // Level 3 (36 tiles wide):
    //   Left platform cols 8-11 row 8, right platform cols 19-23 row 8.
    //   Coins on platforms at row 7, coins on floor at row 9.

    private static final int[][][] COIN_POSITIONS = {
            // Level 1
            {
                    { 3, 9},   // main floor, left section
                    {10, 7},   // raised middle platform
                    {14, 9},   // main floor, just after platform
                    {25, 9},   // main floor, mid-right
                    {35, 9},   // main floor, right
                    {45, 9},   // main floor, far right
            },
            // Level 2
            {
                    { 3, 9},   // main floor, left
                    {10, 7},   // raised left platform
                    {20, 9},   // main floor, centre
                    {32, 7},   // raised right bridge platform
                    {40, 9},   // main floor, right
                    {47, 9},   // main floor, far right
            },
            // Level 3
            {
                    { 3, 9},   // main floor, left
                    {10, 7},   // raised left platform
                    {14, 9},   // main floor, after left platform
                    {21, 7},   // raised right platform
                    {27, 9},   // main floor, after right platform
                    {33, 9},   // main floor, far right
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

    public boolean allCoinsCollected() {
        for (Coin c : coins)
            if (!c.isCollected())
                return false;
        return true;
    }

}