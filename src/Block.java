import java.util.Random;

public class Block {

    enum Shape {
        CUBE,
        ROW,
        LEFT_CHAIR,
        RIGHT_CHAIR,
        PYRAMID,
        DOT
    }

    Random random;

    public Block(){
        setShape();
        setStartPosition();
    }

    private void setStartPosition() {
    }

    private void setShape() {
    }


}
