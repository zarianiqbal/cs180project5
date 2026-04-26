package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import static utilz.Constants.UI.URMButtons.*;

public class GameOverOverlay {

    private Playing playing;
    private UrmButton restartButton;

    public GameOverOverlay(Playing playing) {
        this.playing = playing;
        restartButton = new UrmButton(Game.GAME_WIDTH / 2 - URM_SIZE / 2, (int) (300 * Game.SCALE), URM_SIZE, URM_SIZE, 1);
    }

    public void update() {
        restartButton.update();
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.white);
        g.drawString("Game Over", Game.GAME_WIDTH / 2, 150);
        g.drawString("Press esc to return to Main Menu", Game.GAME_WIDTH / 2, 270);

        restartButton.draw(g);
    }

    public void mousePressed(MouseEvent e) {
        if (restartButton.getBounds().contains(e.getX(), e.getY()))
            restartButton.setMousePressed(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (restartButton.getBounds().contains(e.getX(), e.getY()))
            if (restartButton.isMousePressed()) {
                playing.restartFromDeath();
                Gamestate.state = Gamestate.PLAYING;
            }
        restartButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        restartButton.setMouseOver(false);
        if (restartButton.getBounds().contains(e.getX(), e.getY()))
            restartButton.setMouseOver(true);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // Full reset — clears all checkpoints and returns to level 1
            playing.goToMenu();
            Gamestate.state = Gamestate.MENU;
        }
    }
}