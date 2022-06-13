import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        this.setLayout(new BorderLayout());
        this.add(new GamePanel(), BorderLayout.WEST);
        this.add(new MenuPanel(), BorderLayout.EAST);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setTitle("Tetris");
        this.setResizable(false);
        this.setVisible(true);
    }

}
