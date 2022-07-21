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

    /*private Block movingBlock;
    private Block nextBlock;
    private  ArrayList<Block> fixedBlocks = new ArrayList<>();*/

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

    // --- GAME (LIFE) CYCLE -------------------------------------------------- HERE
/*
    public void startGame() {
        gameLogic.initiateBlocks();
        gameLogic.runGame(true);
        timerGame.start();
    }

    public void gameOver() {
        gameLogic.runGame(false);
        timerGame.stop();
    }

    public void restartGame() {
        gameLogic.getFixedBlocks().clear();
        startGame();
    }*/

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

    // --- VERIFICATIONS ---------------------------------------------------------------

    /*private void verifyFullRows(){
        ArrayList<Integer> fullRows = getYCoordinatesForFullRows();
        if (fullRows.size() > 0) {
            manageFullRow(fullRows);
        }
    }

    private ArrayList<Integer> getYCoordinatesForFullRows() {
        ArrayList<Integer> yCoordinates = new ArrayList<>();

        for (int y = 0; y < HEIGHT; y++) { //For each row
            ArrayList<Unit> unitsOneRow = new ArrayList<>();

            for (Block block : fixedBlocks) { //For each block
                for (Unit unit : block.getUnits()) { //For each unit
                    if (unit.getY() == y) {
                        unitsOneRow.add(unit);
                    }
                }
            }

            if (unitsOneRow.size() == WIDTH / UNIT_SIZE) { //If row is full, save y-coordinate
                yCoordinates.add(y);
            }
        }

        return yCoordinates;
    }

    private boolean isColumnFull() {
        for (Block block : fixedBlocks) {
            if (block.getY() == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isMovingBlockTouchingLeftWall() {
        for (Unit unit : movingBlock.getMostLeftUnits()) {
            if (unit.getX() <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isMovingBlockTouchingRightWall() {
        for (Unit unit : movingBlock.getMostRightUnits()) {
            if (unit.getX() + UNIT_SIZE >= WIDTH) {
                return true;
            }
        }
        return false;
    }

    private boolean isRightBlockedByFixedBlocks() {
        for (Block fixedBlock : fixedBlocks) {
            for (Unit fixedUnit : fixedBlock.getMostLeftUnits()) {
                for (Unit movingUnit : movingBlock.getMostRightUnits()) {
                    if (movingUnit.getX() + UNIT_SIZE == fixedUnit.getX()) {
                        if (movingUnit.getY() == fixedUnit.getY()) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    private boolean isLeftBlockedByFixedBlocks() {
        for (Block fixedBlock : fixedBlocks) {
            for (Unit fixedUnit : fixedBlock.getMostRightUnits()) {
                for (Unit movingUnit : movingBlock.getMostLeftUnits()) {
                    if (movingUnit.getX() == fixedUnit.getX() + UNIT_SIZE) {
                        if (movingUnit.getY() == fixedUnit.getY()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasMovingBlockLandedOnBlocks() {
        if (fixedBlocks.size() > 0) {
            for (Block block : fixedBlocks) {
                for (Unit unit : block.getUnits()) {
                    for (Unit movingUnit : movingBlock.getLowestUnits()) {
                        if (((movingUnit.getY() + UNIT_SIZE == unit.getY()) && (movingUnit.getX() == unit.getX()))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasMovingBlockReachedBottom() {
        for (Unit unit : movingBlock.getLowestUnits()) {
            if ((unit.getY() + UNIT_SIZE) == HEIGHT) {
                return true;
            }
        }
        return false;
    }

    public boolean isRotationPossible() {
        ArrayList<Unit> predictedUnits = movingBlock.predictRotation();

        for (Block block : fixedBlocks) {
            for (Unit fixedUnit : block.getUnits()) {
                for (Unit movingUnit : predictedUnits) {
                    if (fixedUnit.getX() == movingUnit.getX() && fixedUnit.getY() == movingUnit.getY()) {
                        return false;
                    }
                }

            }
        }
        return true;
    }*/

    // --- MANAGE BLOCKS -------------------------------------------------------------

   /* private void removeBlocksWithNoUnits() {
        for (int i = fixedBlocks.size() - 1; i >= 0; i--) {
            Block block = fixedBlocks.get(i);
            if (block.getUnits().isEmpty()) {
                fixedBlocks.remove(block);
            }
        }
    }

    public void removeRow(ArrayList<Integer> yCoordinates) {
        ArrayList<Integer> xCoordinates = new ArrayList<>();

        for (int y : yCoordinates) {
            for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
                int x = i * UNIT_SIZE;
                xCoordinates.add(x);
            }

            for (Block block : fixedBlocks) {
                for (int x : xCoordinates) {
                    block.removeUnit(x, y);
                }
            }
        }
    }

    public void moveEverythingOneRowDown(ArrayList<Integer> yCoordinates) {
        for (int y : yCoordinates) {
            for (Block block : fixedBlocks) {
                block.moveBlockOneRowDown(y);
            }
        }
    }

    public void moveBlockLeft() {
        if (!isMovingBlockTouchingLeftWall() && !isLeftBlockedByFixedBlocks()) {
            movingBlock.moveLeft();
        }
    }

    public void moveBlockRight() {
        if (!isMovingBlockTouchingRightWall() && !isRightBlockedByFixedBlocks()) {
            movingBlock.moveRight();
        }
    }

    public void moveBlockDown() {
        float fallingDelay = 0.2f;
        timeUntilFallingMovement = timeUntilFallingMovement - fallingDelay;
    }

    public void rotateBlock() {
        if (isRotationPossible()) {
            movingBlock.rotate();
        }
    }

    public void landBlock() {
        fixedBlocks.add(movingBlock);
        verifyFullRows();

        if (isColumnFull()) {
            gameOver();
        } else {
            spawnNewBlock();
        }
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

    public void spawnNewBlock() {
        movingBlock = nextBlock;
        movingBlock.setStartPosition();
        createNextBlock();
    }

    public void manageFullRow(ArrayList<Integer> yCoordinates) {
        removeRow(yCoordinates);
        removeBlocksWithNoUnits();
        moveEverythingOneRowDown(yCoordinates);
        increaseScore(yCoordinates.size());
    }

    // --- SCORE -----------------------------------------------------------------------

    private void increaseScore(int rows) {
        gameLogic.increaseScore(rows);
        menuPanel.displayScore();
    }*/

    // --- TICKS -----------------------------------------------------------------------

    long prevWhen = 0;
   /* float timeUntilFallingMovement = 0.0f;
    float timeUntilInputMovement = 0.0f;
    float timeUntilNextRotate = 0.0f;*/
   /* float inputDelay = 0.1f;
    float fallingDelay = 1f;
    float rotationDelay = 0.3f;*/

    @Override
    public void actionPerformed(ActionEvent e) {
        long when = e.getWhen(); //when is this happening?
        long deltaWhenMs = when - prevWhen; //How long has gone sine the last time this happened? (milliseconds)
        float delay = 800f;
        float deltaTime = deltaWhenMs / delay;

        prevWhen = when; //Save the current when

        if (gameLogic.isRunning() && !gameLogic.isPaused()) {
            //timeUntilFallingMovement = timeUntilFallingMovement - deltaTime;
            //timeUntilInputMovement = timeUntilInputMovement - deltaTime;
            //timeUntilNextRotate = timeUntilNextRotate - deltaTime;

            gameLogic.tickInput(deltaTime, myKeyListener);
            gameLogic.tickFallingBlock(deltaTime);
          /*  if (timeUntilFallingMovement <= 0) {
                gameLogic.tickFallingBlock();
                timeUntilFallingMovement = fallingDelay;
            }*/
        }
        repaint();
    }

   /* public void tickFallingBlock() {
        if (gameLogic.hasMovingBlockLandedOnBlocks() || gameLogic.hasMovingBlockReachedBottom()) {
            gameLogic.landBlock();
        } else {
            gameLogic.getMovingBlock().moveOneStepY();
        }
    }

    public void tickInput() {
        if (myKeyListener.leftPressed && timeUntilInputMovement <= 0) {
            gameLogic.moveBlockLeft();
            timeUntilInputMovement = inputDelay;
        }

        if (myKeyListener.rightPressed && timeUntilInputMovement <= 0) {
            gameLogic.moveBlockRight();
            timeUntilInputMovement = inputDelay;
        }

        if (myKeyListener.upPressed && timeUntilNextRotate <= 0) {
            gameLogic.rotateBlock();
            timeUntilNextRotate = rotationDelay;
        }

        if (myKeyListener.downPressed) {
            gameLogic.moveBlockDown(timeUntilFallingMovement);
        }
    }/*

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
                    gameLogic.restartGame();
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
    }*/
}
