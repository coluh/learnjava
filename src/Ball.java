import java.awt.*;

public class Ball {
    final int ballR = 4;
    //    final int ballR = 7;
    public double x, y;
    Color color;
    //int score;
    public double vx, vy, ax, ay, afx, afy;
    boolean isInHole = false;
    //int ballScore = color.ordinal();

    public Ball(double x, double y, Color color, GameBody gameBody) {
        this.x = x;
        this.y = y;
        this.color = color;
        gameBody.repaint();
    }

    public Ball(GameBody ignoredGameBody) {
    }

    public boolean collideWith(Ball that) {
        return Math.pow(Math.abs(this.x - that.x), 2) + Math.pow(Math.abs(this.y - that.y), 2) < Math.pow(this.ballR * 2, 2);
    }

    public void paintSelf(Graphics g) {
        g.setColor(color);
        g.fillOval((int) (x - ballR), (int) (y - ballR), 2 * ballR, 2 * ballR);
    }
}