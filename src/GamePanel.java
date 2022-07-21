import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {

    final private int WIDTH = 240;
    final private int HEIGHT = 500;
    final private int UNIT_SIZE = 20;

    private MyKeyListener myKeyListener = new MyKeyListener();
    private MenuPanel menuPanel;
    private GameLogic gameLogic;
    private Timer timerGame;
    private int delay = 0;
    long prevWhen = 0;

    public GamePanel(MenuPanel menuPanel, GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.menuPanel = menuPanel;
        setupPanel();
        timerGame = new Timer(delay, this);
        timerGame.start();
        setDimensionsGameLogic();
        gameLogic.startGame();
    }

    private void setDimensionsGameLogic() {
        gameLogic.setPanelHeight(HEIGHT);
        gameLogic.setPanelWidth(WIDTH);
        gameLogic.setUnitSize(UNIT_SIZE);
    }

    private void setupPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(myKeyListener);
    }

    // --- DRAW -------------------------------------------------------------------------

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    private void draw(Graphics graphics) {
        for (int i = 0; i < HEIGHT / UNIT_SIZE; i++) {
            graphics.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            graphics.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }

        if (gameLogic.isRunning()) {
            drawMovingBlock(graphics);
            drawExistingBlocks(graphics);
        } else {
            drawGameOver(graphics);
        }
    }

    private void drawGameOver(Graphics graphics) {
        graphics.setColor(new Color(200, 0, 100));
        graphics.setFont(new Font("Bahnschrift SemiBold", Font.BOLD, 32));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("GAME OVER", (WIDTH - metrics.stringWidth("GAME OVER")) / 2, HEIGHT / 2);

        graphics.setFont(new Font("Bahnschrift SemiBold", Font.PLAIN, 24));
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Push space to play", (WIDTH - metrics.stringWidth("Push space to play")) / 2, HEIGHT / 2 + 30);
    }

    private void drawExistingBlocks(Graphics graphics) {
        ArrayList<Block> fixedBlocks = gameLogic.getFixedBlocks();
        for (Block block : fixedBlocks) {
            graphics.setColor(block.getShape().getColor());
            for (Unit unit : block.getUnits()) {
                graphics.fillRect(unit.getX(), unit.getY(), UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    private void drawMovingBlock(Graphics graphics) {
        Block movingBlock = gameLogic.getMovingBlock();
        graphics.setColor(movingBlock.getShape().getColor());

        for (Unit unit : movingBlock.getUnits()) {
            graphics.fillRect(unit.getX(), unit.getY(), UNIT_SIZE, UNIT_SIZE);
        }
    }

    // --- TICKS -----------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        long when = e.getWhen(); //when is this happening?
        long deltaWhenMs = when - prevWhen; //How long has gone since the last time this happened? (milliseconds)
        float delay = 800f;
        float deltaTime = deltaWhenMs / delay;

        prevWhen = when; //Save the current when

        if (gameLogic.isRunning() && !gameLogic.isPaused()) {
            gameLogic.tickInput(deltaTime, myKeyListener);
            gameLogic.tickFallingBlock(deltaTime);
        } else if(!gameLogic.isRunning()){
            gameLogic.pushButtonToRestart(myKeyListener);
        }

        if (gameLogic.isUpdateOfScoreDisplayNeeded()) {
            menuPanel.displayScore();
        }
        repaint();
    }
}
