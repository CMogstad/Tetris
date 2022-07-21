import java.util.ArrayList;

public class GameLogic {

    private int panelWidth;
    private int panelHeight;
    private int unitSize;

    private boolean running = false;
    private boolean paused = false;
    private int score = 0;

    private Block movingBlock;
    private Block nextBlock;
    private final ArrayList<Block> fixedBlocks = new ArrayList<>();

    float timeUntilFallingMovement = 0.0f;
    float timeUntilInputMovement = 0.0f;
    float timeUntilNextRotate = 0.0f;
    float inputDelay = 0.1f;
    float regularFallingDelay = 1f;
    float adjustedFallingDelay = 0.2f;
    float rotationDelay = 0.3f;

    boolean updateOfScoreDisplayNeeded = false;

    // --- GETTERS ------------------------------------------------------

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getScore() {
        return score;
    }

    public Block getMovingBlock() {
        return movingBlock;
    }

    public Block getNextBlock() {
        return nextBlock;
    }

    public ArrayList<Block> getFixedBlocks() {
        return fixedBlocks;
    }


    // --- SETTERS ---------------------------------------------------------

    public void runGame(boolean running) {
        this.running = running;
    }

    public void pauseGame(boolean paused) {
        this.paused = paused;
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
    }

    public void setPanelHeight(int panelHeight) {
        this.panelHeight = panelHeight;
    }

    public void setUnitSize(int unitSize) {
        this.unitSize = unitSize;
    }


    // --- FUNCTIONALITY ------------------------------------------------------------

    public void increaseScore(int rows) {
        score += 100 * (rows + (rows - 1));
    }


    // --- VERIFICATIONS ---------------------------------------------------------------

    public void verifyFullRows() {
        ArrayList<Integer> fullRows = getYCoordinatesForFullRows();
        if (fullRows.size() > 0) {
            manageFullRow(fullRows);
            updateOfScoreDisplayNeeded = true;
        } else {
            updateOfScoreDisplayNeeded = false;
        }
    }

    public ArrayList<Integer> getYCoordinatesForFullRows() {
        ArrayList<Integer> yCoordinates = new ArrayList<>();

        for (int y = 0; y < panelHeight; y++) { //For each row
            ArrayList<Unit> unitsOneRow = new ArrayList<>();

            for (Block block : fixedBlocks) { //For each block
                for (Unit unit : block.getUnits()) { //For each unit
                    if (unit.getY() == y) {
                        unitsOneRow.add(unit);
                    }
                }
            }

            if (unitsOneRow.size() == panelWidth / unitSize) { //If row is full, save y-coordinate
                yCoordinates.add(y);
            }
        }

        return yCoordinates;
    }

    public boolean isColumnFull() {
        for (Block block : fixedBlocks) {
            if (block.getY() == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMovingBlockTouchingLeftWall() {
        for (Unit unit : movingBlock.getMostLeftUnits()) {
            if (unit.getX() <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMovingBlockTouchingRightWall() {
        for (Unit unit : movingBlock.getMostRightUnits()) {
            if (unit.getX() + unitSize >= panelWidth) {
                return true;
            }
        }
        return false;
    }

    public boolean isRightBlockedByFixedBlocks() {
        for (Block fixedBlock : fixedBlocks) {
            for (Unit fixedUnit : fixedBlock.getMostLeftUnits()) {
                for (Unit movingUnit : movingBlock.getMostRightUnits()) {
                    if (movingUnit.getX() + unitSize == fixedUnit.getX()) {
                        if (movingUnit.getY() == fixedUnit.getY()) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    public boolean isLeftBlockedByFixedBlocks() {
        for (Block fixedBlock : fixedBlocks) {
            for (Unit fixedUnit : fixedBlock.getMostRightUnits()) {
                for (Unit movingUnit : movingBlock.getMostLeftUnits()) {
                    if (movingUnit.getX() == fixedUnit.getX() + unitSize) {
                        if (movingUnit.getY() == fixedUnit.getY()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasMovingBlockLandedOnBlocks() {
        if (fixedBlocks.size() > 0) {
            for (Block block : fixedBlocks) {
                for (Unit unit : block.getUnits()) {
                    for (Unit movingUnit : movingBlock.getLowestUnits()) {
                        if (((movingUnit.getY() + unitSize == unit.getY()) && (movingUnit.getX() == unit.getX()))) {
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
            if ((unit.getY() + unitSize) == panelHeight) {
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
    }

    // --- MANAGE BLOCKS -------------------------------------------------------------

    public void removeBlocksWithNoUnits() {
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
            for (int i = 0; i < panelWidth / unitSize; i++) {
                int x = i * unitSize;
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
        //adjustedFallingDelay = 0.2f;
        timeUntilFallingMovement = timeUntilFallingMovement - adjustedFallingDelay;
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
        nextBlock = new Block(panelHeight, panelWidth, unitSize);
    }

    public void createMovingBlock() {
        movingBlock = new Block(panelHeight, panelWidth, unitSize);
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

    //--- TICKS ---------------------------------------------------------------------------

    public boolean isUpdateOfScoreDisplayNeeded() {
        return updateOfScoreDisplayNeeded;
    }

    public void tickFallingBlock(float deltaTime) {
        timeUntilFallingMovement = timeUntilFallingMovement - deltaTime;

        if (timeUntilFallingMovement <= 0) {
            if (hasMovingBlockLandedOnBlocks() || hasMovingBlockReachedBottom()) {
                landBlock();
            } else {
                getMovingBlock().moveOneStepY();
            }

            timeUntilFallingMovement = regularFallingDelay;
        }
    }

    public void tickInput(float deltaTime, MyKeyListener myKeyListener) {
        timeUntilInputMovement = timeUntilInputMovement - deltaTime;
        timeUntilNextRotate = timeUntilNextRotate - deltaTime;

        if (running) {
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
    }

    public void pushButtonToRestart(MyKeyListener myKeyListener){
        if (myKeyListener.spacePressed) {
            restartGame();
        }
    }

    // --- GAME (LIFE) CYCLE --------------------------------------------------

    public void startGame() {
        initiateBlocks();
        runGame(true);
    }

    public void gameOver() {
        runGame(false);
    }

    public void restartGame() {
        resetScore();
        fixedBlocks.clear();
        startGame();
    }

    private void resetScore() {
        score = 0;
        updateOfScoreDisplayNeeded = true;
    }
}
