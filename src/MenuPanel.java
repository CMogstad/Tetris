import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {

    final int WIDTH = 120;
    final int HEIGHT = 500;
    final int UNIT_SIZE = 20;

    int score = 0;
    JLabel scoreLabel;
    JLabel scoreAmount;
    JLabel nextBlockLabel;
    Block nextBlock;
    JButton pauseButton;
    SpringLayout springLayout = new SpringLayout();
    GameLogic gameLogic;

    public MenuPanel(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(new Color(100, 100, 120));
        createScoreLabel();
        createNextBlockLabel();
        createPauseButton();
        this.setLayout(springLayout);
        this.add(scoreLabel);
        this.add(scoreAmount);
        this.add(nextBlockLabel);
        this.add(pauseButton);
        this.setVisible(true);
    }

    private void createPauseButton() {
        pauseButton = new JButton("PAUSE");
        pauseButton.setBackground(Color.white);
        pauseButton.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 12));
        pauseButton.setBorder(new LineBorder(Color.BLACK, 1));
        pauseButton.setPreferredSize(new Dimension(80,30));
        pauseButton.addActionListener(this);
        pauseButton.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, pauseButton, 300, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pauseButton, 0, SpringLayout.HORIZONTAL_CENTER, this);
    }

    private void setPauseButtonText() {
        if (gameLogic.isPaused()) {
            pauseButton.setText("CONTINUE");
        } else {
            pauseButton.setText("PAUSE");
        }
    }

    private void createNextBlockLabel() {
        nextBlockLabel = new JLabel("NEXT");
        nextBlockLabel.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 24));
        nextBlockLabel.setForeground(Color.BLACK);

        springLayout.putConstraint(SpringLayout.NORTH, nextBlockLabel, 110, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, nextBlockLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);
    }

    private void createScoreLabel() {
        scoreLabel = new JLabel("SCORE");
        scoreAmount = new JLabel(score + "");

        scoreLabel.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 24));
        scoreAmount.setFont(new Font("Bahnschrift SemiBold", Font.PLAIN, 24));
        scoreLabel.setForeground(Color.BLACK);
        scoreAmount.setForeground(Color.BLACK);

        springLayout.putConstraint(SpringLayout.NORTH, scoreLabel, 20, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, scoreLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);

        springLayout.putConstraint(SpringLayout.NORTH, scoreAmount, 50, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, scoreAmount, 0, SpringLayout.HORIZONTAL_CENTER, this);
    }

    public void displayScore() {
        scoreAmount.setText(gameLogic.getScore() + "");
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawNextBlock(graphics);
    }

    public void drawNextBlock(Graphics graphics) {
        graphics.setColor(nextBlock.getShape().getColor());

        for (Unit unit : nextBlock.getUnits()) {
            graphics.fillRect(unit.getX() + UNIT_SIZE * 2, unit.getY() + 8 * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
        }
    }

    public void setNextBlock(Block block) {
        nextBlock = block;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pauseButton) {
            if (gameLogic.isPaused()) {
                gameLogic.pauseGame(false);
            } else {
                gameLogic.pauseGame(true);
            }
            setPauseButtonText();
        }
    }
}
