public class PhysicalCompute {
    GameBody gB;
    final double u = 0.003;
    Ball[] balls = new Ball[22];

    public PhysicalCompute(GameBody gB) {
        this.gB = gB;
        for (int i = 0; i < 6; i++) {
            this.balls[i] = gB.balls[i];
        }
        this.balls[6] = gB.whiteBall;
    }

    public void move() {
        for (int i = 0; i < 6 + 1; i++) {
            balls[i].realX += balls[i].vx;
            balls[i].realY += balls[i].vy;
            balls[i].x = (int) balls[i].realX;
            balls[i].y = (int) balls[i].realY;
            balls[i].ax = balls[i].afx - u * balls[i].vx;
            balls[i].ay = balls[i].afy - u * balls[i].vy;
            balls[i].vx += balls[i].ax;
            balls[i].vy += balls[i].ay;
            if (Math.pow(Math.abs(balls[i].vx), 2) + Math.pow(Math.abs(balls[i].vy), 2) < 0.005) {
                balls[i].vx = 0;
                balls[i].vy = 0;
            }
            checkHitWall(balls[i]);
        }
        checkHitBall();
        gB.repaint();
    }

    public void checkHitBall() {
        double temp;
        for (int i = 0; i < 6 + 1; i++) {
            for (int j = i + 1; j < 6 + 1; j++) {
                if (balls[i].collideWith(balls[j])) {
                    double ky = balls[j].realY - balls[i].realY;
                    double kx = balls[j].realX - balls[i].realX;
                    Speed p = collideAlgorithm(balls[i].vx, balls[i].vy, balls[j].vx, balls[j].vy, kx, ky);
                    balls[i].vx = p.v1x;
                    balls[i].vy = p.v1y;
                    balls[j].vx = p.v2x;
                    balls[j].vy = p.v2y;
                }
            }
        }
    }

    private Speed collideAlgorithm(double v1x, double v1y, double v2x, double v2y, double kx, double ky) {
        double t = v2x * kx + v2y * ky - v1x * kx - v1y * ky;
        double s = kx * kx + ky * ky;
        Speed p = new Speed();
        p.v1x = v1x + t * kx / s;
        p.v1y = v1y + t * ky / s;
        p.v2x = v2x - t * kx / s;
        p.v2y = v2y - t * ky / s;
        return p;
    }

    public void checkHitWall(Ball ball) {
        if (ball.x < gB.OX | ball.x > gB.OX + gB.tableWidth) {
            ball.vx = -ball.vx;
            if (ball.x < gB.OX) {
                ball.x = gB.OX;
            } else {
                ball.x = gB.OX + gB.tableWidth;
            }
        }
        if (ball.y < gB.OY | ball.y > gB.OY + gB.tableHeight) {
            ball.vy = -ball.vy;
            if (ball.y < gB.OY) {
                ball.y = gB.OY;
            } else {
                ball.y = gB.OY + gB.tableHeight;
            }
        }
    }
}
class Speed {
    double v1x;
    double v1y;
    double v2x;
    double v2y;
}