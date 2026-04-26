package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import static utilz.Constants.UI.URMButtons.*;

public class GameOverOverlay {

    private Playing playing;
    private UrmButton retryButton;
    private UrmButton menuButton;

    public GameOverOverlay(Playing playing) {
        this.playing = playing;
        initButtons();
    }

    private void initButtons() {
        int centreX = Game.GAME_WIDTH / 2;
        int btnSpacing = (int) (74 * Game.SCALE);
        int btnY = (int) (280 * Game.SCALE);

        retryButton = new UrmButton(centreX - btnSpacing / 2 - URM_SIZE / 2, btnY, URM_SIZE, URM_SIZE, 1);
        menuButton  = new UrmButton(centreX + btnSpacing / 2 - URM_SIZE / 2, btnY, URM_SIZE, URM_SIZE, 2);
    }

    public void update() {
        retryButton.update();
        menuButton.update();
    }

    public void draw(Graphics g) {
        // Dark overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // "Game Over" title
        Font titleFont = new Font("Arial", Font.BOLD, (int) (22 * Game.SCALE));
        g.setFont(titleFont);
        g.setColor(new Color(220, 60, 60));
        FontMetrics fm = g.getFontMetrics(titleFont);
        String title = "Game Over";
        g.drawString(title, Game.GAME_WIDTH / 2 - fm.stringWidth(title) / 2, (int) (180 * Game.SCALE));

        // Hint text
        Font hintFont = new Font("Arial", Font.PLAIN, (int) (7 * Game.SCALE));
        g.setFont(hintFont);
        g.setColor(new Color(180, 180, 180));
        FontMetrics hfm = g.getFontMetrics(hintFont);
        String hint = "Press ESC to return to Main Menu";
        g.drawString(hint, Game.GAME_WIDTH / 2 - hfm.stringWidth(hint) / 2, (int) (220 * Game.SCALE));

        retryButton.draw(g);
        menuButton.draw(g);
    }

    public void mousePressed(MouseEvent e) {
        if (isIn(e, retryButton))
            retryButton.setMousePressed(true);
        else if (isIn(e, menuButton))
            menuButton.setMousePressed(true);
    }

    public void mouseReleased(MouseEvent e) {
        if (isIn(e, retryButton)) {
            if (retryButton.isMousePressed()) {
                playing.restartFromDeath();
                Gamestate.state = Gamestate.PLAYING;
            }
        } else if (isIn(e, menuButton)) {
            if (menuButton.isMousePressed()) {
                playing.goToMenu();
                Gamestate.state = Gamestate.MENU;
            }
        }
        retryButton.resetBools();
        menuButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        retryButton.setMouseOver(false);
        menuButton.setMouseOver(false);
        if (isIn(e, retryButton))
            retryButton.setMouseOver(true);
        else if (isIn(e, menuButton))
            menuButton.setMouseOver(true);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playing.goToMenu();
            Gamestate.state = Gamestate.MENU;
        }
    }

    private boolean isIn(MouseEvent e, UrmButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }

}