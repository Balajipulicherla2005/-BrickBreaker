import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreaker {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        GamePanel gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(gamePanel);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 90;
    private int lives = 5;

    private Timer timer;
    private int delay = 0;

    private int playerX = 240;
    private int ballPosX = 180;
    private int ballPosY = 350;
    private int ballDirX = -1;
    private int ballDirY = -2;

    private MapGenerator map;

    public GamePanel() {
        map = new MapGenerator(10, 9);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 900, 900);

        // Drawing map
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.orange);
        g.fillRect(0, 0, 4, 500);
        g.fillRect(0, 0, 300, 4);
        g.fillRect(797, 0, 4, 600);

        // Scores
        g.setColor(Color.green);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 580, 30);
        g.drawString("Lives: " + lives, 20, 30);

        // Paddle
        g.setColor(Color.red);
        g.fillRect(playerX, 550, 100, 8);

        // Ball
        g.setColor(Color.blue);
        g.fillOval(ballPosX, ballPosY, 20, 20);

        // Game Over
        if (ballPosY > 570) {
            lives--;
            if (lives > 0) {
                resetBall();
            } else {
                play = false;
                g.setColor(Color.blue);
                g.setFont(new Font("serif", Font.BOLD, 30));
                g.drawString("Game Over, Score: " + score, 250, 300);
                g.drawString("Press Enter to Restart", 230, 340);
            }
        }

        // Winning
        if (totalBricks == 0) {
            play = false;
            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won! Score: " + score, 250, 300);
            g.drawString("Press Enter to Restart", 230, 340);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballDirY = -ballDirY;
            }

            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + brickRect.width) {
                                ballDirX = -ballDirX;
                            } else {
                                ballDirY = -ballDirY;
                            }

                            break A;
                        }
                    }
                }
            }

            ballPosX += ballDirX;
            ballPosY += ballDirY;

            if (ballPosX < 0 || ballPosX > 770) {
                ballDirX = -ballDirX;
            }

            if (ballPosY < 0) {
                ballDirY = -ballDirY;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 700) {
                playerX = 700;
            } else {
                moveRight();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballPosX = 240;
                ballPosY = 350;
                ballDirX = -1;
                ballDirY = -2;
                playerX = 310;
                score = 0;
                totalBricks = 27;
                lives = 3;
                map = new MapGenerator(3, 9);
                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 20;
    }

    public void moveLeft() {
        play = true;
        playerX -= 20;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void resetBall() {
        ballPosX = 240;
        ballPosY = 350;
        ballDirX = -1;
        ballDirY = -2;
        play = false;
    }
}

class MapGenerator {
    public int[][] map;
    public int brickWidth;
    public int brickHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = 1;
            }
        }

        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
