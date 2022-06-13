import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {

    //Unit size

    public GamePanel(){
        this.setPreferredSize(new Dimension(300,500));
        this.setBackground(Color.black);
    }

    //draw

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public class MyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }


}
