import java.awt.*;

public class Ball {
    //final int ballR = 4;
    final int ballR = 7;
    public int x, y;
    public double realX, realY;
    Color color;
    //int score;
    public double vx, vy, ax, ay, afx, afy;
    //int ballScore = color.ordinal();

    public Ball(int x, int y, Color color, GameBody gameBody) {
        this.x = x;
        this.y = y;
        this.realX = (int) x;
        this.realY = (int) y;
        this.color = color;
        gameBody.repaint();
    }

    public Ball(GameBody gameBody) {
    }

    public boolean collideWith(Ball that) {
        return Math.pow(Math.abs(this.x - that.x), 2) + Math.pow(Math.abs(this.y - that.y), 2) < Math.pow(this.ballR * 2, 2);
    }

    public void paintSelf(Graphics g) {
        g.setColor(color);
        g.fillOval(x - ballR, y - ballR, 2 * ballR, 2 * ballR);
    }
}