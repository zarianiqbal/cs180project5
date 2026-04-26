package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import entities.CheckpointManager;
import entities.CoinManager;
import entities.EnemyManager;
import entities.HealthPotionManager;
import entities.Player;
import entities.SpikeManager;
import levels.LevelManager;
import main.Game;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.LoadSave;
import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private CoinManager coinManager;
    private SpikeManager spikeManager;
    private HealthPotionManager healthPotionManager;
    private CheckpointManager checkpointManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private boolean paused = false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    private int maxLvlOffsetX;

    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver;
    private boolean lvlCompleted;

    private int currentLevelIndex = 0;

    private float[] savedCheckpointSpawn = null;
    private int savedCheckpointLevelIndex = -1;

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));

        calcLvlOffset();
        loadStartLevel();
    }

    public void loadNextLevel() {
        // Save checkpoint to locals before partial reset wipes savedCheckpointSpawn
        float[] tempSpawn = null;
        int tempLevel = -1;

        float[] currentSpawn = checkpointManager.getActiveSpawn();
        if (currentSpawn != null) {
            tempSpawn = new float[]{currentSpawn[0], currentSpawn[1]};
            tempLevel = currentLevelIndex;
        } else if (savedCheckpointSpawn != null) {
            tempSpawn = new float[]{savedCheckpointSpawn[0], savedCheckpointSpawn[1]};
            tempLevel = savedCheckpointLevelIndex;
        }

        currentLevelIndex++;

        // Partial reset — skips enemyManager.resetAllEnemies() so dead crabs
        // on the previous level stay dead if the player returns via checkpoint.
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        savedCheckpointSpawn = null;
        savedCheckpointLevelIndex = -1;
        player.resetAll();
        coinManager.resetAllCoins();
        spikeManager.resetAllSpikes();
        healthPotionManager.resetAllPotions();
        checkpointManager.resetCheckpoint();

        // Restore saved checkpoint after partial reset
        savedCheckpointSpawn = tempSpawn;
        savedCheckpointLevelIndex = tempLevel;

        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        coinManager.loadCoins(currentLevelIndex);
        spikeManager.loadSpikes(currentLevelIndex);
        healthPotionManager.loadPotions(currentLevelIndex);
        checkpointManager.loadCheckpoint(currentLevelIndex);
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        coinManager.loadCoins(currentLevelIndex);
        spikeManager.loadSpikes(currentLevelIndex);
        healthPotionManager.loadPotions(currentLevelIndex);
        checkpointManager.loadCheckpoint(currentLevelIndex);
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        coinManager = new CoinManager();
        spikeManager = new SpikeManager();
        healthPotionManager = new HealthPotionManager();
        checkpointManager = new CheckpointManager();

        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
    }

    @Override
    public void update() {
        if (paused) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        } else if (gameOver) {
            gameOverOverlay.update();
        } else {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            coinManager.update(player.getHitbox());

            int spikeDamage = spikeManager.update(player.getHitbox());
            if (spikeDamage > 0)
                player.changeHealth(-spikeDamage);

            int healAmount = healthPotionManager.update(player.getHitbox());
            if (healAmount > 0)
                player.changeHealth(healAmount);

            checkpointManager.update(player.getHitbox());

            float[] spawn = checkpointManager.getActiveSpawn();
            if (spawn != null) {
                player.setCheckpointSpawn(spawn[0], spawn[1]);
                savedCheckpointSpawn = new float[]{spawn[0], spawn[1]};
                savedCheckpointLevelIndex = currentLevelIndex;
            }

            checkCloseToBorder();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        drawClouds(g);

        levelManager.draw(g, xLvlOffset);
        coinManager.draw(g, xLvlOffset);
        spikeManager.draw(g, xLvlOffset);
        healthPotionManager.draw(g, xLvlOffset);
        checkpointManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
    }

    public void restartFromDeath() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;

        float[] currentCheckpoint = checkpointManager.getActiveSpawn();

        if (currentCheckpoint != null) {
            // Respawn at current level checkpoint — all crabs reset, player gets invincibility frames
            enemyManager.resetAllEnemies();
            player.resetAll();
            player.setInvincible();
            coinManager.resetAllCoins();
            spikeManager.resetAllSpikes();
            healthPotionManager.resetAllPotions();

        } else if (savedCheckpointSpawn != null) {
            // Return to the level where the last checkpoint was saved
            currentLevelIndex = savedCheckpointLevelIndex;
            levelManager.resetLevelEnemies(currentLevelIndex);
            levelManager.loadLevel(currentLevelIndex);
            coinManager.loadCoins(currentLevelIndex);
            spikeManager.loadSpikes(currentLevelIndex);
            healthPotionManager.loadPotions(currentLevelIndex);
            checkpointManager.loadCheckpoint(currentLevelIndex);
            checkpointManager.forceActivate();
            player.setCheckpointSpawn(savedCheckpointSpawn[0], savedCheckpointSpawn[1]);
            player.resetAll();
            player.setInvincible();
            xLvlOffset = 0;

        } else {
            // No checkpoint — full restart from level 1
            currentLevelIndex = 0;
            levelManager.resetAllLevelEnemies();
            levelManager.loadLevel(0);
            player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
            player.resetAll();
            coinManager.loadCoins(currentLevelIndex);
            spikeManager.loadSpikes(currentLevelIndex);
            healthPotionManager.loadPotions(currentLevelIndex);
            checkpointManager.loadCheckpoint(currentLevelIndex);
            xLvlOffset = 0;
        }
    }

    public void goToMenu() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        savedCheckpointSpawn = null;
        savedCheckpointLevelIndex = -1;
        currentLevelIndex = 0;
        xLvlOffset = 0;

        levelManager.resetAllLevelEnemies();
        levelManager.loadLevel(0);
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        player.resetAll();
        coinManager.loadCoins(0);
        spikeManager.loadSpikes(0);
        healthPotionManager.loadPotions(0);
        checkpointManager.loadCheckpoint(0);
    }

    public void resetAll() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        savedCheckpointSpawn = null;
        savedCheckpointLevelIndex = -1;
        player.resetAll();
        enemyManager.resetAllEnemies();
        coinManager.resetAllCoins();
        spikeManager.resetAllSpikes();
        healthPotionManager.resetAllPotions();
        checkpointManager.resetCheckpoint();
    }

    public void checkLevelCompleted() {
        if (coinManager.allCoinsCollected())
            lvlCompleted = true;
    }

    public void setLevelCompleted(boolean levelCompleted) {
        this.lvlCompleted = levelCompleted;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public boolean isLastLevel() {
        return levelManager.isLastLevel();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver)
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        else
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }
    }

    public void mouseDragged(MouseEvent e) {
        if (!gameOver)
            if (paused)
                pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mousePressed(e);
        else if (paused)
            pauseOverlay.mousePressed(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseReleased(e);
        else if (paused)
            pauseOverlay.mouseReleased(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameOver)
            gameOverOverlay.mouseMoved(e);
        else if (paused)
            pauseOverlay.mouseMoved(e);
        else if (lvlCompleted)
            levelCompletedOverlay.mouseMoved(e);
    }

    public void setMaxLvlOffset(int lvlOffset) {
        this.maxLvlOffsetX = lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

}