import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    public GameFrame(GameLogic gameLogic) {
        this.setLayout(new BorderLayout());

        MenuPanel menuPanel = new MenuPanel(gameLogic);
        GamePanel gamePanel = new GamePanel(menuPanel, gameLogic);
        this.add(menuPanel, BorderLayout.EAST);
        this.add(gamePanel, BorderLayout.WEST);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setVisible(true);
    }
}
