//import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class WhiteBall extends Ball {
    public Point mousePos;
    public final Point windowPos;
    private final GameBody gB;
    Color color = Color.WHITE;
    double lastX, lastY;
    public boolean whiteOK = false;
    public boolean showLine = false;
    private final MouseListener m1 = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
//            super.mouseClicked(e);
            mousePos = e.getLocationOnScreen();
            x = (double) mousePos.x - windowPos.x;
            y = (double) mousePos.y - windowPos.y;
            whiteOK = true;
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
        if(!showLine){
            return;
        }
        //have whiteBall.x,y
        double mx = mousePos.x - windowPos.x;
        double my = mousePos.y - windowPos.y;
        //have mouseX,Y
        g.drawLine((int) x, (int) y, (int) mx, (int) my);
    }
}
