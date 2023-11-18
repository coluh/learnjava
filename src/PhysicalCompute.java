public class PhysicalCompute {
    GameBody gB;
    final double u = 0.003;
    Ball[] balls = new Ball[21 + 1];

    public PhysicalCompute(GameBody gB) {
        this.gB = gB;
        System.arraycopy(gB.balls, 0, this.balls, 0, gB.balls.length);
        this.balls[gB.balls.length] = gB.whiteBall;
    }

    public void move() {
        for (Ball ball : balls) {
            if (ball.isInHole) {
                continue;
            }
            ball.x += ball.vx;
            ball.y += ball.vy;
            ball.ax = ball.afx - u * ball.vx;
            ball.ay = ball.afy - u * ball.vy;
            ball.vx += ball.ax;
            ball.vy += ball.ay;
            if (Math.pow(Math.abs(ball.vx), 2) + Math.pow(Math.abs(ball.vy), 2) < 0.005) {
                ball.vx = 0;
                ball.vy = 0;
            }
            checkHitWall(ball);
            checkInHole(ball);
        }
        checkHitBall();
        gB.repaint();
    }

    public void checkHitBall() {
        for (int i = 0; i < balls.length; i++) {
            for (int j = i + 1; j < balls.length; j++) {
                if (!(balls[i].isInHole || balls[j].isInHole) && balls[i].collideWith(balls[j])) {
                    double ky = balls[j].y - balls[i].y;
                    double kx = balls[j].x - balls[i].x;
                    balls[i].x -= kx / 10;
                    balls[i].y -= ky / 10;
                    balls[j].x += kx / 10;
                    balls[j].y += ky / 10;
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
        if (ball.x < gB.OX + ball.ballR | ball.x > gB.OX + gB.tableWidth - ball.ballR) {
            ball.vx = -ball.vx;
            if (ball.x < gB.OX + ball.ballR) {
                ball.x = gB.OX + ball.ballR;
            } else {
                ball.x = gB.OX + gB.tableWidth - ball.ballR;
            }
        }
        if (ball.y < gB.OY + ball.ballR | ball.y > gB.OY + gB.tableHeight - ball.ballR) {
            ball.vy = -ball.vy;
            if (ball.y < gB.OY + ball.ballR) {
                ball.y = gB.OY + ball.ballR;
            } else {
                ball.y = gB.OY + gB.tableHeight - ball.ballR;
            }
        }
    }

    public void checkInHole(Ball ball) {
        for (int ix = 60; ix < 700; ix += 290) {
            if ((Math.pow(Math.abs(ball.x - ix), 2) + Math.pow(Math.abs(ball.y - 90), 2) < Math.pow(gB.holeR, 2)) || (Math.pow(Math.abs(ball.x - ix), 2) + Math.pow(Math.abs(ball.y - (94 + gB.tableHeight)), 2) < Math.pow(gB.holeR, 2))) {
                ball.isInHole = true;
                break;
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