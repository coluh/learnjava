import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class GameBody extends JFrame implements MouseListener, MouseMotionListener {

    public final int OX = 62;
    public final int OY = 92;
    public int tableWidth = 576;
    public int tableHeight = 288;
    Ball[] balls = new Ball[21];
    WhiteBall whiteBall;
    //    private Point mousePos;
    public Point windowPos;
    //    private GameBody gB;
    public final double v0 = 3.0;
    /*
     * 四个要改的地方
     * 1. 初速度
     * 2. u的值
     * 3. 发射计算中的sleep间隙
     * 4. 可忽略的最大速度*/
    private Image offScreenImage;

    public void init() {
        this.setVisible(true);
//        this.gB = this;
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setTitle("The First Try");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setBackground(Color.GRAY);
        this.windowPos = getLocationOnScreen();
        //创建21个球
        balls[0] = new Ball(OX + 116, OY + 190, Color.YELLOW, this);
        balls[1] = new Ball(OX + 116, OY + 98, Color.GREEN, this);
        balls[2] = new Ball(OX + 116, OY + 144, Color.ORANGE, this);
        balls[3] = new Ball(OX + 288, OY + 144, Color.BLUE, this);
        balls[4] = new Ball(OX + 432, OY + 144, Color.PINK, this);
        balls[5] = new Ball(OX + 525, OY + 144, Color.BLACK, this);
        for (int i = 0, k = 6; i < 5; i++) {
            for (int j = 0; j <= i; j++, k++) {
                //ballR * gen3 ~= 7
                balls[k] = new Ball(OX + 442 + 7 * i, OY + 144 - 4 * i + 8 * j, Color.RED, this);
            }
        }
        whiteBall = new WhiteBall(this);
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(700, 500);
        }
        Graphics gOff = offScreenImage.getGraphics();
        //body
        gOff.setColor(Color.GRAY);
        gOff.fillRect(0, 0, 700, 500);
        gOff.setColor(Color.WHITE);
        gOff.fillRect(52, 82, tableWidth + 20, tableHeight + 20);
        gOff.setColor(Color.ORANGE);
        gOff.fillRect(OX - 5, OY - 5, tableWidth + 10, tableHeight + 10);
        gOff.setColor(new Color(0, 100, 0));
        gOff.fillRect(OX, OY, tableWidth, tableHeight);
        gOff.setColor(Color.BLACK);
        for (int ix = 60; ix < 700; ix += 290) {
            //int holeR = 7;
            int holeR = 12;
            gOff.fillOval(ix - holeR, 90 - holeR, 2 * holeR, 2 * holeR);
            gOff.fillOval(ix - holeR, 94 + tableHeight - holeR, 2 * holeR, 2 * holeR);
        }
        //ball
        if(!whiteBall.isInHole){
            whiteBall.paintSelf(gOff);
        }
        for (int i = 0; i < balls.length; i++) {
            if(balls[i].isInHole){
                continue;
            }
            balls[i].paintSelf(gOff);
        }
        g.drawImage(offScreenImage, 0, 0, null);
    }

    private final MouseListener m1 = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
//            super.mouseClicked(e);
            whiteBall.whiteOK = true;
            removeMouseListener(m1);
        }
    };

    public void play() {
        //检测白球放好了没
        while (!whiteBall.whiteOK) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        whiteBall.whiteOK = false;
        // 瞄准画线
        this.addMouseListener(m1);
        whiteBall.showLine = true;
        while (!whiteBall.whiteOK) {
            whiteBall.mousePos = MouseInfo.getPointerInfo().getLocation();
            this.repaint();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        whiteBall.showLine = false;
        //发射
        //mousePos - whiteBall
        whiteBall.vx = Math.abs(v0 * Math.cos(Math.atan((double) (whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y) / (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x))));
        whiteBall.vy = Math.abs(v0 * Math.sin(Math.atan((double) (whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y) / (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x))));
        if (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x < 0) {
            whiteBall.vx *= -1;
        }
        if (whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y < 0) {
            whiteBall.vy *= -1;
        }
        whiteBall.lastX = whiteBall.x;
        whiteBall.lastY = whiteBall.y;
        while (!allBallStatic()) {
            new PhysicalCompute(this).move();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(whiteBall.isInHole){
            whiteBall.isInHole = false;
            whiteBall.x = whiteBall.lastX;
            whiteBall.y = whiteBall.lastY;
            whiteBall.realX = whiteBall.x;
            whiteBall.realY = whiteBall.y;
        }
        //
    }

    private boolean allBallStatic() {
        if(whiteBall.isInHole){
            whiteBall.vx = 0;
            whiteBall.vy = 0;
        }
        if (Math.abs(whiteBall.vx) > 0 || Math.abs(whiteBall.vy) > 0) {
            return false;
        }
        for (int i = 0; i < balls.length; i++) {
            if(balls[i].isInHole){
                continue;
            }
            if (Math.abs(balls[i].vx) > 0 || Math.abs(balls[i].vy) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
