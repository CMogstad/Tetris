import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    int score = 0;
    JLabel scoreLabel = new JLabel("Score: " + score);

    public MenuPanel(){
        this.setPreferredSize(new Dimension(100,500));
        this.setBackground(Color.gray);
        this.add(scoreLabel);
    }


}
