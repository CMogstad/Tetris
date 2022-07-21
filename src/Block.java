import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Block {

    enum Shape {
        CUBE(new Color(150, 0, 200)),
        ROW(new Color(0, 200, 250)),
        LEFT_CHAIR(new Color(250, 220, 0)),
        RIGHT_CHAIR(new Color(100, 200, 0)),
        PYRAMID(new Color(200, 0, 100)),
        LEFT_L(new Color(250, 100, 0)),
        RIGHT_L(new Color(0, 0, 200));

        Color color;

        Shape(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    Shape shape;
    int screenHeight;
    int screenWidth;
    int unitSize;
    int x = 0;
    int y = 0;
    int state = 0;
    ArrayList<Unit> units = new ArrayList<>();

    public Block(int screenHeight, int screenWidth, int unitSize) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.unitSize = unitSize;
        setShape();
        generateUnitsIntoShape();
    }

    public void setStartPosition() {
        for(Unit unit : units){
            int x = unit.getX() + (((screenWidth / unitSize) / 2) * unitSize - unitSize);
            unit.setX(x);
        }
    }

    private void setShape() {
        int shapeId = (int) (Math.random() * 7);

        switch (shapeId) {
            case 0 -> shape = Shape.CUBE;
            case 1 -> shape = Shape.ROW;
            case 2 -> shape = Shape.LEFT_CHAIR;
            case 3 -> shape = Shape.RIGHT_CHAIR;
            case 4 -> shape = Shape.PYRAMID;
            case 5 -> shape = Shape.LEFT_L;
            case 6 -> shape = Shape.RIGHT_L;
        }
    }

    private void generateUnitsIntoShape() {
        switch (shape) {
            case CUBE -> generateCubeUnits();
            case ROW -> generateRowUnits();
            case LEFT_CHAIR -> generateLeftChair();
            case RIGHT_CHAIR -> generateRightChair();
            case PYRAMID -> generatePyramid();
            case LEFT_L -> generateLeftL();
            case RIGHT_L -> generateRightL();
        }
    }

    private void generateRightL() {
        Unit unit1 = new Unit(x, y + unitSize);
        Unit unit2 = new Unit(x + unitSize, y + unitSize);
        Unit unit3 = new Unit(x + 2 * unitSize, y + unitSize);
        Unit unit4 = new Unit(x + 2 * unitSize, y);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generateLeftL() {
        Unit unit1 = new Unit(x, y + unitSize);
        Unit unit2 = new Unit(x + unitSize, y + unitSize);
        Unit unit3 = new Unit(x + 2 * unitSize, y + unitSize);
        Unit unit4 = new Unit(x, y);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generatePyramid() {
        Unit unit1 = new Unit(x + unitSize, y);
        Unit unit2 = new Unit(x, y + unitSize);
        Unit unit3 = new Unit(x + unitSize, y + unitSize);
        Unit unit4 = new Unit(x + 2 * unitSize, y + unitSize);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generateCubeUnits() {
        Unit unit1 = new Unit(x, y);
        Unit unit2 = new Unit(x + unitSize, y);
        Unit unit3 = new Unit(x, y + unitSize);
        Unit unit4 = new Unit(x + unitSize, y + unitSize);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generateRowUnits() {
        Unit unit1 = new Unit(x - unitSize, y);
        Unit unit2 = new Unit(x, y);
        Unit unit3 = new Unit(x + unitSize, y);
        Unit unit4 = new Unit(x + 2 * unitSize, y);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generateLeftChair() {
        Unit unit1 = new Unit(x + unitSize, y);
        Unit unit2 = new Unit(x, y + unitSize);
        Unit unit3 = new Unit(x + unitSize, y + unitSize);
        Unit unit4 = new Unit(x, y + unitSize * 2);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    private void generateRightChair() {
        Unit unit1 = new Unit(x, y);
        Unit unit2 = new Unit(x , y + unitSize);
        Unit unit3 = new Unit(x +  unitSize, y + unitSize);
        Unit unit4 = new Unit(x + unitSize, y + unitSize * 2);

        units.add(unit1);
        units.add(unit2);
        units.add(unit3);
        units.add(unit4);
    }

    public ArrayList<Unit> predictRotation() {
        int predictedState = calcNextState();
        ArrayList<Unit> predictedUnits = cloneUnits();

        switch (shape) {
            case ROW -> rotateRow(predictedState, predictedUnits);
            case LEFT_CHAIR -> rotateLeftChair(predictedState, predictedUnits);
            case RIGHT_CHAIR -> rotateRightChair(predictedState, predictedUnits);
            case PYRAMID -> rotatePyramid(predictedState, predictedUnits);
            case LEFT_L -> rotateLeftL(predictedState, predictedUnits);
            case RIGHT_L -> rotateRightL(predictedState, predictedUnits);
        }

        return predictedUnits;
    }

    public void rotate() {
        state = calcNextState();

        switch (shape) {
            case ROW -> rotateRow(state, units);
            case LEFT_CHAIR -> rotateLeftChair(state, units);
            case RIGHT_CHAIR -> rotateRightChair(state, units);
            case PYRAMID -> rotatePyramid(state, units);
            case LEFT_L -> rotateLeftL(state, units);
            case RIGHT_L -> rotateRightL(state, units);
        }
    }

    private void rotateRightL(int newState, ArrayList<Unit> unitsToRotate) {
        int x0 = units.get(0).getX();
        int y0 = units.get(0).getY();
        int x2 = units.get(2).getX();
        int y2 = units.get(2).getY();
        int x3 = units.get(3).getX();
        int y3 = units.get(3).getY();

        if (newState == 0) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(2).setX(x2 + unitSize);
            unitsToRotate.get(2).setY(y2 + unitSize);

            unitsToRotate.get(3).setX(x3 + 2 * unitSize);
            unitsToRotate.get(3).setY(y3);

        } else if (newState == 1) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(2).setX(x2 - unitSize);
            unitsToRotate.get(2).setY(y2 + unitSize);

            unitsToRotate.get(3).setX(x3);
            unitsToRotate.get(3).setY(y3 + 2 * unitSize);

        } else if (newState == 2) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(2).setX(x2 - unitSize);
            unitsToRotate.get(2).setY(y2 - unitSize);

            unitsToRotate.get(3).setX(x3 - 2 * unitSize);
            unitsToRotate.get(3).setY(y3);

        } else if (newState == 3) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(2).setX(x2 + unitSize);
            unitsToRotate.get(2).setY(y2 - unitSize);

            unitsToRotate.get(3).setX(x3);
            unitsToRotate.get(3).setY(y3 - 2 * unitSize);
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    private void rotateLeftL(int newState, ArrayList<Unit> unitsToRotate) {
        int x0 = units.get(0).getX();
        int y0 = units.get(0).getY();
        int x2 = units.get(2).getX();
        int y2 = units.get(2).getY();
        int x3 = units.get(3).getX();
        int y3 = units.get(3).getY();

        if (newState == 0) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(2).setX(x2 + unitSize);
            unitsToRotate.get(2).setY(y2 + unitSize);

            unitsToRotate.get(3).setX(x3);
            unitsToRotate.get(3).setY(y3 - 2 * unitSize);

        } else if (newState == 1) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(2).setX(x2 - unitSize);
            unitsToRotate.get(2).setY(y2 + unitSize);

            unitsToRotate.get(3).setX(x3 + 2 * unitSize);
            unitsToRotate.get(3).setY(y3);

        } else if (newState == 2) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(2).setX(x2 - unitSize);
            unitsToRotate.get(2).setY(y2 - unitSize);

            unitsToRotate.get(3).setX(x3);
            unitsToRotate.get(3).setY(y3 + 2 * unitSize);

        } else if (newState == 3) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(2).setX(x2 + unitSize);
            unitsToRotate.get(2).setY(y2 - unitSize);

            unitsToRotate.get(3).setX(x3 - 2 * unitSize);
            unitsToRotate.get(3).setY(y3);
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    private void rotatePyramid(int newState, ArrayList<Unit> unitsToRotate) {
        int x0 = units.get(0).getX();
        int y0 = units.get(0).getY();
        int x1 = units.get(1).getX();
        int y1 = units.get(1).getY();
        int x3 = units.get(3).getX();
        int y3 = units.get(3).getY();

        if (newState == 0) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(1).setX(x1 - unitSize);
            unitsToRotate.get(1).setY(y1 - unitSize);

            unitsToRotate.get(3).setX(x3 + unitSize);
            unitsToRotate.get(3).setY(y3 + unitSize);

        } else if (newState == 1) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(1).setX(x1 + unitSize);
            unitsToRotate.get(1).setY(y1 - unitSize);

            unitsToRotate.get(3).setX(x3 - unitSize);
            unitsToRotate.get(3).setY(y3 + unitSize);

        } else if (newState == 2) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(1).setX(x1 + unitSize);
            unitsToRotate.get(1).setY(y1 + unitSize);

            unitsToRotate.get(3).setX(x3 - unitSize);
            unitsToRotate.get(3).setY(y3 - unitSize);

        } else if (newState == 3) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(1).setX(x1 - unitSize);
            unitsToRotate.get(1).setY(y1 + unitSize);

            unitsToRotate.get(3).setX(x3 + unitSize);
            unitsToRotate.get(3).setY(y3 - unitSize);
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    private void rotateRightChair(int newState, ArrayList<Unit> unitsToRotate) {
        //Rotate around 2nd unit (1)

        int y0 = units.get(0).getY();
        int x1 = units.get(1).getX();
        int y1 = units.get(1).getY();
        int x3 = units.get(3).getX();
        int y3 = units.get(3).getY();

        if (newState == 0 || newState == 2) {
            unitsToRotate.get(0).setY(y0 - 2 * unitSize);

            unitsToRotate.get(1).setX(x1 - unitSize);
            unitsToRotate.get(1).setY(y1 - unitSize);

            unitsToRotate.get(3).setX(x3 - unitSize);
            unitsToRotate.get(3).setY(y3 + unitSize);

        } else if (newState == 1 || newState == 3) {
            unitsToRotate.get(0).setY(y0 + 2 * unitSize);

            unitsToRotate.get(1).setX(x1 + unitSize);
            unitsToRotate.get(1).setY(y1 + unitSize);

            unitsToRotate.get(3).setX(x3 + unitSize);
            unitsToRotate.get(3).setY(y3 - unitSize);
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    private void rotateLeftChair(int newState, ArrayList<Unit> unitsToRotate) {
        //Rotate around 2nd unit

        int x0 = units.get(0).getX();
        int y0 = units.get(0).getY();
        int x1 = units.get(1).getX();
        int y1 = units.get(1).getY();
        int x3 = units.get(3).getX();

        if (newState == 0 || newState == 2) {
            unitsToRotate.get(0).setX(x0 + unitSize);
            unitsToRotate.get(0).setY(y0 - unitSize);

            unitsToRotate.get(1).setX(x1 - unitSize);
            unitsToRotate.get(1).setY(y1 - unitSize);

            unitsToRotate.get(3).setX(x3 - (unitSize * 2));

        } else if (newState == 1 || newState == 3) {
            unitsToRotate.get(0).setX(x0 - unitSize);
            unitsToRotate.get(0).setY(y0 + unitSize);

            unitsToRotate.get(1).setX(x1 + unitSize);
            unitsToRotate.get(1).setY(y1 + unitSize);

            unitsToRotate.get(3).setX(x3 + (unitSize * 2));
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    private void rotateRow(int newState, ArrayList<Unit> unitsToRotate) {
        int rotationX = units.get(2).getX();
        int rotationY = units.get(2).getY();
        int movingPositions = 2;

        for (Unit unit : unitsToRotate) {
            if (newState == 0 || newState == 2) {
                unit.setX(rotationX - unitSize * movingPositions);
                unit.setY(rotationY);
            } else if (newState == 1 || newState == 3) {
                unit.setX(rotationX);
                unit.setY(rotationY + movingPositions * unitSize);
            }
            movingPositions--;
        }

        moveStepsAwayFromLeftWall(unitsToRotate);
        moveStepsAwayFromRightWall(unitsToRotate);
    }

    // ... OTHER ........................................................

    private int calcNextState() {
        if (state == 3) {
            return 0;
        } else {
            return state + 1;
        }
    }

    private ArrayList<Unit> cloneUnits() {
        ArrayList<Unit> predictedUnits = new ArrayList<>();

        for (Unit unit : units) {
            predictedUnits.add(unit.cloneUnit());
        }

        return predictedUnits;
    }

    // ... MOVEMENT ........................................................

    public void moveOneStepY() {
        y += unitSize;

        for (Unit unit : units) {
            unit.setY(unit.getY() + unitSize);
        }
    }

    public void moveLeft() {
        x -= unitSize;

        for (Unit unit : units) {
            unit.setX(unit.getX() - unitSize);
        }
    }

    public void moveRight() {
        x += unitSize;

        for (Unit unit : units) {
            unit.setX(unit.getX() + unitSize);
        }
    }

    public void moveBlockOneRowDown(int y) {
        for (Unit unit : units) {
            if (unit.getY() < y) {
                int currentY = unit.getY();
                unit.setY(currentY + unitSize);
            }
        }
    }

    public void moveStepsAwayFromLeftWall(ArrayList<Unit> unitsToMove) {
        int stepsToMove = 0;

        for (Unit unit : unitsToMove) {
            if (unit.getX() < 0) {
                stepsToMove++;
            }
        }

        for (Unit unit : unitsToMove) {
            int x = unit.getX();
            unit.setX(x + stepsToMove * unitSize);
        }
    }

    public void moveStepsAwayFromRightWall(ArrayList<Unit> unitsToMove) {
        int stepsToMove = 0;

        for (Unit unit : unitsToMove) {
            if (unit.getX() + unitSize > screenWidth) {
                stepsToMove++;
            }
        }

        for (Unit unit : unitsToMove) {
            int x = unit.getX();
            unit.setX(x - stepsToMove * unitSize);
        }
    }

    // ... GETTERS ........................................................

    public int getY() {
        return y;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public Shape getShape() {
        return shape;
    }

    public ArrayList<Unit> getMostRightUnits() {
        ArrayList<Unit> mostRightUnits = new ArrayList<>();

        int minY = units.stream().mapToInt(Unit::getY).min().orElseThrow(NoSuchElementException::new);
        int maxY = units.stream().mapToInt(Unit::getY).max().orElseThrow(NoSuchElementException::new);
        int rows = (maxY - minY + unitSize) / unitSize;

        for (int i = 0; i < rows; i++) {
            int finalMinY = minY;
            List<Unit> unitsInRow = units.stream().filter(u -> u.getY() == finalMinY).collect(Collectors.toList());
            Unit mostRight = unitsInRow.stream().max(Comparator.comparing(Unit::getX)).orElseThrow(NoSuchElementException::new);
            mostRightUnits.add(mostRight);
            minY = minY + unitSize;
        }

        return mostRightUnits;
    }

    public ArrayList<Unit> getMostLeftUnits() {
        ArrayList<Unit> mostLeftUnits = new ArrayList<>();

        int minY = units.stream().mapToInt(Unit::getY).min().orElseThrow(NoSuchElementException::new);
        int maxY = units.stream().mapToInt(Unit::getY).max().orElseThrow(NoSuchElementException::new);
        int rows = (maxY - minY + unitSize) / unitSize;

        for (int i = 0; i < rows; i++) {
            int finalMinY = minY;
            List<Unit> unitsInRow = units.stream().filter(u -> u.getY() == finalMinY).collect(Collectors.toList());
            Unit mostLeft = unitsInRow.stream().min(Comparator.comparing(Unit::getX)).orElseThrow(NoSuchElementException::new);
            mostLeftUnits.add(mostLeft);
            minY = minY + unitSize;
        }

        return mostLeftUnits;
    }

    public ArrayList<Unit> getLowestUnits() {
        ArrayList<Unit> lowestUnits = new ArrayList<>();

        int minX = units.stream().mapToInt(Unit::getX).min().orElseThrow(NoSuchElementException::new);
        int maxX = units.stream().mapToInt(Unit::getX).max().orElseThrow(NoSuchElementException::new);
        int columns = (maxX - minX + unitSize) / unitSize;

        for (int i = 0; i < columns; i++) {
            int finalMinX = minX;
            List<Unit> unitsInColumn = units.stream().filter(u -> u.getX() == finalMinX).collect(Collectors.toList());
            Unit lowest = unitsInColumn.stream().max(Comparator.comparing(Unit::getY)).orElseThrow(NoSuchElementException::new);
            lowestUnits.add(lowest);
            minX = minX + unitSize;
        }

        return lowestUnits;
    }

    // ... REMOVE ...................................................................

    public void removeUnit(int x, int y) {
        for (int i = units.size() - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (unit.getX() == x && unit.getY() == y) {
                units.remove(unit);
            }
        }
    }
}
