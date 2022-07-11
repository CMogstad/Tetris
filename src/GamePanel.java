import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements ActionListener {

    final int WIDTH = 240;
    final int HEIGHT = 500;
    final int UNIT_SIZE = 20;

    MyKeyListener myKeyListener = new MyKeyListener();
    Timer timerGame;
    Block movingBlock;
    Block nextBlock;
    int delay = 0;

    MenuPanel menuPanel;
    ArrayList<Block> blocks = new ArrayList<>();
    GameLogic gameLogic;

    public GamePanel(MenuPanel menuPanel, GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(myKeyListener);

        this.menuPanel = menuPanel;

        startGame();
    }

    public void startGame() {
        initiateBlocks(); //TODO: KEEP CODE LIKE THIS??
        gameLogic.runGame(true);
        timerGame = new Timer(delay, this);
        timerGame.start();
    }

    public void restartGame() {
        blocks.clear();
        startGame();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void draw(Graphics graphics) {
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
        for (Block block : blocks) {
            graphics.setColor(block.getShape().getColor());
            for (Unit unit : block.getUnits()) {
                graphics.fillRect(unit.getX(), unit.getY(), UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void drawMovingBlock(Graphics graphics) {
        graphics.setColor(movingBlock.getShape().getColor());

        for (Unit unit : movingBlock.getUnits()) {
            graphics.fillRect(unit.getX(), unit.getY(), UNIT_SIZE, UNIT_SIZE);
        }
    }

    public void checkRow() {
        ArrayList<Integer> yCoordinates = new ArrayList<>();

        for (int y = 0; y < HEIGHT; y++) { //For each row
            ArrayList<Unit> unitsOneRow = new ArrayList<>();

            for (Block block : blocks) { //For each block
                for (Unit unit : block.getUnits()) { //For each unit
                    if (unit.getY() == y) {
                        unitsOneRow.add(unit);
                    }
                }
            }

            if (unitsOneRow.size() == WIDTH / UNIT_SIZE) {
                yCoordinates.add(y);
            }
        }

        if (yCoordinates.size() > 0) {
            removeRow(yCoordinates);
            removeBlocksWithNoUnits();
            moveEverythingOneRowDown(yCoordinates);
            increaseScore(yCoordinates.size());
        }

    }

    private void removeBlocksWithNoUnits() {
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (block.getUnits().isEmpty()) {
                blocks.remove(block);
            }
        }
    }

    public void checkColumn() {
        for (Block block : blocks) {
            if (block.getY() == 0) {
                gameOver();
            }
        }
    }

    public void removeRow(ArrayList<Integer> yCooridnates) {
        ArrayList<Integer> xCoordinates = new ArrayList<>();

        for (int y : yCooridnates) {
            for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
                int x = i * UNIT_SIZE;
                xCoordinates.add(x);
            }

            for (Block block : blocks) {
                for (int x : xCoordinates) {
                    block.removeUnit(x, y);
                }
            }
        }
    }

    private void increaseScore(int rows) {
        gameLogic.increaseScore(rows);
        menuPanel.displayScore();
    }

    public void moveEverythingOneRowDown(ArrayList<Integer> yCoordinates) {
        for (int y : yCoordinates) {
            for (Block block : blocks) {
                block.moveBlockOneRowDown(y);
            }
        }
    }

    public void tickFallingBlock() {
        boolean stop = false;

        for (Unit unit : movingBlock.getLowestUnits()) {
            if ((unit.getY() + UNIT_SIZE) == HEIGHT) { //Has block reached bottom?
                stop = true;
            }
        }

        if (blocks.size() > 0) { //Has block touched other blocks?
            for (Block block : blocks) {
                for (Unit unit : block.getUnits()) {
                    for (Unit movingUnit : movingBlock.getLowestUnits()) {
                        if (((movingUnit.getY() + UNIT_SIZE == unit.getY()) && (movingUnit.getX() == unit.getX()))) {
                            stop = true;
                            break;
                        }
                    }
                }
            }
        }

        if (stop) {
            landingBlock();
        } else {
            movingBlock.moveOneStepY();
        }
    }

    public void tickInput() {
        if (myKeyListener.leftPressed && timeUntilInputMovement <= 0) {
            moveBlockLeft();
            timeUntilInputMovement = inputDelay;
        }

        if (myKeyListener.rightPressed && timeUntilInputMovement <= 0) {
            moveBlockRight();
            timeUntilInputMovement = inputDelay;
        }

        if (myKeyListener.upPressed && timeUntilNextRotate <= 0) {
            rotateBlock();
            timeUntilNextRotate = rotationDelay;
        }

        if (myKeyListener.downPressed) {
            moveBlockDown();
        }
    }

    public void moveBlockLeft() {
        boolean canMoveLeft = true;

        canMoveLeft = !isMovingBlockNextToLeftWall();

        for (Block block : blocks) {
            if (isLeftBlocked(block)) {
                canMoveLeft = false;
                break;
            }
        }

        if (canMoveLeft) {
            movingBlock.moveLeft();
        }
    }

    public boolean isMovingBlockNextToLeftWall() {
        for (Unit unit : movingBlock.getMostLeftUnits()) {
            if (unit.getX() <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMovingBlockNextToRightWall() {
        for (Unit unit : movingBlock.getMostRightUnits()) {
            if (unit.getX() + UNIT_SIZE >= WIDTH) {
                return true;
            }
        }
        return false;
    }

    public void moveBlockRight() {
        boolean canMoveRight = true;

        canMoveRight = !isMovingBlockNextToRightWall();

        for (Block block : blocks) {
            if (isRightBlocked(block)) {
                canMoveRight = false;
            }
        }

        if (canMoveRight) {
            movingBlock.moveRight();
        }
    }

    public boolean isRightBlocked(Block fixedBlock) {
        for (Unit fixedUnit : fixedBlock.getMostLeftUnits()) {
            for (Unit movingUnit : movingBlock.getMostRightUnits()) {
                if (movingUnit.getX() + UNIT_SIZE == fixedUnit.getX()) {
                    if (movingUnit.getY() == fixedUnit.getY()) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public boolean isLeftBlocked(Block fixedBlock) {
        for (Unit fixedUnit : fixedBlock.getMostRightUnits()) {
            for (Unit movingUnit : movingBlock.getMostLeftUnits()) {
                if (movingUnit.getX() == fixedUnit.getX() + UNIT_SIZE) {
                    if (movingUnit.getY() == fixedUnit.getY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void moveBlockDown() {
        timeUntilFallingMovement = timeUntilFallingMovement - 0.2f;
    }

    public void rotateBlock() {
        boolean rotationAllowed = true;
        ArrayList<Unit> predictedUnits = movingBlock.predictRotation();

        //Predict rotation
        for (Block block : blocks) {
            for (Unit fixedUnit : block.getUnits()) {
                for (Unit movingUnit : predictedUnits) {
                    if (fixedUnit.getX() == movingUnit.getX() && fixedUnit.getY() == movingUnit.getY()) {
                        rotationAllowed = false;
                        break;
                    }
                }
                if (!rotationAllowed) { //TODO: MAKE THIS CODE LESS AWKWARD
                    break;
                }
            }
            if (!rotationAllowed) {
                break;
            }
        }

        if (rotationAllowed) {
            movingBlock.rotate();
        }
    }

    public void landingBlock() {
        blocks.add(movingBlock);
        checkRow();
        checkColumn();
        spawnBlock();
    }

    public void initiateBlocks() {
        createMovingBlock();
        createNextBlock();
    }

    public void createNextBlock() {
        nextBlock = new Block(HEIGHT, WIDTH, UNIT_SIZE);
        menuPanel.setNextBlock(nextBlock);
    }

    public void createMovingBlock() {
        movingBlock = new Block(HEIGHT, WIDTH, UNIT_SIZE);
        movingBlock.setStartPosition();
    }

    public void spawnBlock() {
        movingBlock = nextBlock;
        movingBlock.setStartPosition();
        createNextBlock();
    }

    public void gameOver() {
        gameLogic.runGame(false);
        timerGame.stop();
    }

    long prevWhen = 0;
    float timeUntilFallingMovement = 0.0f;
    float timeUntilInputMovement = 0.0f;
    float timeUntilNextRotate = 0.0f;
    float inputDelay = 0.1f;
    float fallingDelay = 1f;
    float rotationDelay = 0.3f;

    @Override
    public void actionPerformed(ActionEvent e) {
        long when = e.getWhen(); //when is this happening?
        long deltaWhenMs = when - prevWhen; //How long has gone sine the last time this happened? (milliseconds)
        float deltaTime = deltaWhenMs / 800f;

        prevWhen = when; //Save the current when

        if (gameLogic.isRunning() && !gameLogic.isPaused()) {
            timeUntilFallingMovement = timeUntilFallingMovement - deltaTime;
            timeUntilInputMovement = timeUntilInputMovement - deltaTime;
            timeUntilNextRotate = timeUntilNextRotate - deltaTime;

            tickInput();

            if (timeUntilFallingMovement <= 0) {
                tickFallingBlock();
                timeUntilFallingMovement = fallingDelay;
            }
        }
        repaint();
    }

    private class MyKeyListener implements KeyListener {
        boolean leftPressed = false;
        boolean rightPressed = false;
        boolean upPressed = false;
        boolean downPressed = false;

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (gameLogic.isRunning()) {
                switch (e.getKeyCode()) {
                    case 37 -> leftPressed = true;
                    case 38 -> upPressed = true;
                    case 39 -> rightPressed = true;
                    case 40 -> downPressed = true;
                }
            } else {
                if (e.getKeyCode() == 32) {
                    restartGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case 37 -> leftPressed = false;
                case 38 -> upPressed = false;
                case 39 -> rightPressed = false;
                case 40 -> downPressed = false;
            }
        }
    }
}
