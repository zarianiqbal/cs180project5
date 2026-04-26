package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Game;

public class CheckpointManager {

    private Checkpoint checkpoint;

    // Level 1 (index 0) has no checkpoint — the player always restarts
    // from the beginning of the level if they die there.
    // Indices here map directly: index 1 = level 2, index 2 = level 3.
    private static final int[][] CHECKPOINT_POSITIONS = {
            {24, 9},   // Level 2
            {25, 9},   // Level 3
    };

    public CheckpointManager() {
    }

    public void loadCheckpoint(int levelIndex) {
        // Level 1 (index 0) has no checkpoint
        int posIndex = levelIndex - 1;
        if (levelIndex == 0 || posIndex >= CHECKPOINT_POSITIONS.length) {
            checkpoint = null;
            return;
        }
        int[] pos = CHECKPOINT_POSITIONS[posIndex];
        float x = pos[0] * Game.TILES_SIZE;
        float y = pos[1] * Game.TILES_SIZE;
        checkpoint = new Checkpoint(x, y);
    }

    public void update(Rectangle2D.Float playerHitbox) {
        if (checkpoint != null && !checkpoint.isActivated())
            checkpoint.checkPlayerCollision(playerHitbox);
    }

    public void draw(Graphics g, int xLvlOffset) {
        if (checkpoint != null)
            checkpoint.draw(g, xLvlOffset);
    }

    public void forceActivate() {
        if (checkpoint != null)
            checkpoint.forceActivate();
    }

    public float[] getActiveSpawn() {
        if (checkpoint != null && checkpoint.isActivated())
            return new float[]{checkpoint.getSpawnX(), checkpoint.getSpawnY()};
        return null;
    }

    public void resetCheckpoint() {
        if (checkpoint != null)
            checkpoint.reset();
    }

}