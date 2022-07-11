public class GameLogic {

    private boolean running = false;
    private boolean paused = false;
    private int score = 0;

    public void increaseScore(int rows) {
        score += 100 * (rows + (rows - 1));
    }

    public void runGame(boolean running) {
        this.running = running;
    }

    public void pauseGame(boolean paused) {
        this.paused = paused;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getScore() {
        return score;
    }
}
