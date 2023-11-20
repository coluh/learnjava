//import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.CountDownLatch;

public class WhiteBall extends Ball {
    public Point mousePos;
    public final Point windowPos;
    private final GameBody gB;
    Color color = Color.WHITE;
    double lastX, lastY;
    public boolean showLine = false;
    public final CountDownLatch latchForWhiteBall = new CountDownLatch(1);
    private final MouseListener m1 = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
//            super.mouseClicked(e);
            mousePos = e.getLocationOnScreen();
            x = (double) mousePos.x - windowPos.x;
            y = (double) mousePos.y - windowPos.y;
            latchForWhiteBall.countDown();
//            System.out.println("Nice");
            gB.removeMouseMotionListener(m2);
            gB.removeMouseListener(m1);
        }
    };
    private final MouseMotionListener m2 = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            mousePos = e.getLocationOnScreen();
            x = mousePos.x - windowPos.x;
            y = mousePos.y - windowPos.y;
            gB.repaint();
        }
    };

    public WhiteBall(GameBody gameBody) {
        super(gameBody);
        this.gB = gameBody;
        windowPos = gB.getLocationOnScreen();
        gB.addMouseListener(m1);
        gB.addMouseMotionListener(m2);
    }

    @Override
    public boolean collideWith(Ball that) {
        return super.collideWith(that);
    }

    @Override
    public void paintSelf(Graphics g) {
        g.setColor(color);
        g.fillOval((int) (x - ballR), (int) (y - ballR), 2 * ballR, 2 * ballR);
        // 为什么这里不这样写就无法让球变白呢?  时间有限, 以后再来探究吧!
        //  知道了, 调用super方法还是什么的来着就可以
        if (!showLine) {
            return;
        }
        //have whiteBall.x,y
        //here the direction kx,ky
        Ball preBall_0 = new Ball();
        preBall_0.x = x;
        preBall_0.y = y;
        preBall_0.vx = mousePos.x - windowPos.x - x;
        preBall_0.vy = mousePos.y - windowPos.y - y;
        Ball preBall_1 = computeLine(preBall_0);
        if (preBall_1 != null) {
            g.setColor(color);
            g.drawLine((int) preBall_0.x, (int) preBall_0.y, (int) preBall_1.x, (int) preBall_1.y);
            g.drawOval((int) (preBall_1.x - ballR), (int) (preBall_1.y - ballR), ballR * 2, ballR * 2);
            Ball preBall_2 = computeLine(preBall_1);
            if (preBall_2 != null) {
                g.setColor(color);
                g.drawLine((int) preBall_1.x, (int) preBall_1.y, (int) preBall_2.x, (int) preBall_2.y);
                g.drawOval((int) (preBall_2.x - ballR), (int) (preBall_2.y - ballR), ballR * 2, ballR * 2);
            }
        }
        ////
    }

    private Ball computeLine(Ball preBall) {
        double backX = preBall.x;
        double backY = preBall.y;
        double stepX = preBall.vx;
        double stepY = preBall.vy;
        while (Math.abs(stepX) > 1 || Math.abs(stepY) > 1) {
            stepX /= 2.0;
            stepY /= 2.0;
        }
        if (stepX == 0 || stepY == 0) {
            return null;
        }
        /*
         *附加一种情况:
         *如果该球会进洞, 也返回Null, 以免画出不存在的路线
         */
        Ball newBall = new Ball();
        boolean ballInWay = false;
        while (!ballInWay) {
            preBall.x += stepX;
            preBall.y += stepY;
            if (preBall.x < gB.OX + ballR || preBall.x > gB.OX + gB.tableWidth - ballR) {
                newBall.vx = -preBall.vx;
                newBall.vy = preBall.vy;
                break;
            }
            if (preBall.y < gB.OY + ballR || preBall.y > gB.OY + gB.tableHeight - ballR) {
                newBall.vx = preBall.vx;
                newBall.vy = -preBall.vy;
                break;
            }
            for (int i = 0; i < gB.balls.length; i++) {
                if (4 * ballR * ballR > (Math.pow(Math.abs(gB.balls[i].x - preBall.x), 2) + Math.pow(Math.abs(gB.balls[i].y - preBall.y), 2))) {
                    double kx = gB.balls[i].x - preBall.x;
                    double ky = gB.balls[i].y - preBall.y;
                    double k2 = kx * kx + ky * ky;
                    double temp = 1.0;
                    newBall.vx = preBall.vx - temp * kx * (preBall.vx * kx + preBall.vy * ky) / k2;
                    newBall.vy = preBall.vy - temp * ky * (preBall.vx * kx + preBall.vy * ky) / k2;
                    preBall.x -= kx / 10;
                    preBall.y -= ky / 10;
                    ballInWay = true;
                    break;
                }
            }

        }
        newBall.x = preBall.x;
        newBall.y = preBall.y;
        preBall.x = backX;
        preBall.y = backY;
        return newBall;
        //end while
    }
}
