import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameBody extends JFrame implements MouseListener, MouseMotionListener {

    public final int OX = 62;
    public final int OY = 92;
    public int tableWidth = 576;
    public int tableHeight = 288;
    public int holeR = 21;//should be 7
    Ball[] balls = new Ball[21];
    WhiteBall whiteBall;
    public boolean isPlaying = true;
    //    private Point mousePos;
    public Point windowPos;
    private CountDownLatch latch;
    private ScheduledExecutorService schedulerForAim;
    public final double v0 = 10.0;
    /*
     * 四个要改的地方
     * 1. 初速度
     * 2. u的值
     * 3. 发射计算中的delay
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
        try {
            whiteBall.latchForWhiteBall.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
            gOff.fillOval(ix - holeR, 90 - holeR, 2 * holeR, 2 * holeR);
            gOff.fillOval(ix - holeR, 94 + tableHeight - holeR, 2 * holeR, 2 * holeR);
        }
        //ball
        if(!whiteBall.isInHole){
            whiteBall.paintSelf(gOff);
        }
        for (Ball ball : balls) {
            if (ball.isInHole) {
                continue;
            }
            ball.paintSelf(gOff);
        }
        g.drawImage(offScreenImage, 0, 0, null);
    }

    private final MouseListener m1 = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            latch.countDown();
            schedulerForAim.shutdown();
            removeMouseListener(m1);
        }
    };

    public void play() {
        //
        // 瞄准画线
        whiteBall.showLine = true;
        this.addMouseListener(m1);
        latch = new CountDownLatch(1);
        schedulerForAim = Executors.newScheduledThreadPool(1);
        schedulerForAim.scheduleWithFixedDelay(()->{
            whiteBall.mousePos = MouseInfo.getPointerInfo().getLocation();
            this.repaint();
        }, 0, 10, TimeUnit.MILLISECONDS);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        whiteBall.showLine = false;
        //发射
        //mousePos - whiteBall
        whiteBall.vx = Math.abs(v0 * Math.cos(Math.atan((whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y) / (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x))));
        whiteBall.vy = Math.abs(v0 * Math.sin(Math.atan((whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y) / (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x))));
        if (whiteBall.mousePos.x - whiteBall.windowPos.x - whiteBall.x < 0) {
            whiteBall.vx *= -1;
        }
        if (whiteBall.mousePos.y - whiteBall.windowPos.y - whiteBall.y < 0) {
            whiteBall.vy *= -1;
        }
        whiteBall.lastX = whiteBall.x;
        whiteBall.lastY = whiteBall.y;
        latch = new CountDownLatch(1);
        ScheduledExecutorService schedulerForShoot = Executors.newScheduledThreadPool(1);
        schedulerForShoot.scheduleAtFixedRate(()->{
            new PhysicalCompute(this).move();
            if(allBallStatic()){
                latch.countDown();
                schedulerForShoot.shutdown();
            }
        }, 0, 8, TimeUnit.MILLISECONDS);
        // 125Hz !!!
        /* 这里使用WithFixedDelay时出现未知错误, 具体表现为延时莫名变长 */
        // 故转而选用AtFixedRate
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(whiteBall.isInHole){
            whiteBall.isInHole = false;
            whiteBall.x = whiteBall.lastX;
            whiteBall.y = whiteBall.lastY;
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
        int numberOfBallsInHole = 0;
        for (Ball ball : balls) {
            if (ball.isInHole) {
                numberOfBallsInHole++;
                continue;
            }
            if (Math.abs(ball.vx) > 0 || Math.abs(ball.vy) > 0) {
                return false;
            }
        }
        if(numberOfBallsInHole == balls.length){
            isPlaying = false;
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
