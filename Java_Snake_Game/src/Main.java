import javax.swing.*;
import java.awt.*;

// 使用 Timer 時要手動 import 的東西
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;

public class Main extends JPanel implements KeyListener {
    public static int width =400;
    public static int height =400;
    public static final int CELLSIZE=20;
    // 計算會有幾行列+欄
    public static int row=height/CELLSIZE;
    public static int column=width/CELLSIZE;
    private Snake snake;
    private Fruit fruit;
    private Timer t; // Timer 是內建的 data type
    private int speed=100;
    private static String direction;
    // 做一個布林值變數，讓新畫面還沒被渲染好之前，任何的 keypress 都會無效
    // 避免亂按但畫面還沒渲染好，讓蛇莫名其妙咬到自己
    private boolean allowKeyPress;
    private int score;
    private int highest_score;

    // 把 filename.txt 放到玩家的電腦桌面
    String desktop=System.getProperty("user.home")+"/Desktop";
    String myFile=desktop+"filename.txt";

    // 做出蛇、果實的物件
    public Main(){
        read_highest_score();
        // 監聽 Main 物件發生的事件
        addKeyListener(this);
        reset();
    }
    // 下面 reset method 會用到的setTimer method
    private void setTimer(){
        t=new Timer();
        // 在固定的區間讓 Timer 重新渲染畫面
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                repaint();
            }
        },0,speed); // 毫秒計算，寫 1000 = 1 秒之意
    }

    // 重新開始遊戲的 method
    private void reset(){
        score=0;
        if(snake!=null){
            snake.getSnakeBody().clear();
        }

        direction="Right"; // 讓蛇的初始前進方向是往右邊移動
        allowKeyPress=true;// 遊戲一開始時，玩家的 keypress 有效

        snake=new Snake();
        fruit=new Fruit();
        setTimer();
    }

    // 畫出蛇、果實
    public void paintComponent(Graphics g){
        // 確認新的蛇的畫面有沒有咬到自己的身體，
        ArrayList<Node> snake_body=snake.getSnakeBody();
        Node head=snake_body.get(0);
        for(int i=1;i<snake.getSnakeBody().size();i++){
            if(snake_body.get(i).x==head.x && snake_body.get(i).y==head.y){
                allowKeyPress=false;
                t.cancel(); //暫停 Timer
                t.purge();
                int response=JOptionPane.showOptionDialog(this,"Game Over! Your score is "+score+". Would you like to start over?","Game Over",JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,null,JOptionPane.YES_OPTION);

                write_a_file(score);

                switch (response){
                    case JOptionPane.CLOSED_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.YES_OPTION:
                        reset();
                        return;
                }
            }
        }


        // 畫黑色背景
        g.fillRect(0,0,width,height);

        // 先畫出果實，才不會有果實蓋在蛇身上的狀況
        fruit.drawFruit(g);

        // 畫出蛇
        snake.drawSnake(g);

        // 每次重新渲染，都會移除蛇的尾巴 Node 並增加一個新的 Node 到蛇頭
        // 得到蛇頭 Node 的 x,y 座標
        int snakeX=snake.getSnakeBody().get(0).x;
        int snakeY=snake.getSnakeBody().get(0).y;
        // 判斷蛇的方向，再調整蛇頭的 x,y 值
        if(direction.equals("Left")){
            snakeX-=CELLSIZE;
        } else if (direction.equals("Up")) {
            snakeY-=CELLSIZE;
        } else if (direction.equals("Right")) {
            snakeX+=CELLSIZE;
        } else if (direction.equals("Down")) {
            snakeY+=CELLSIZE;
        }
        // 設定新的蛇頭 node 的 x,y 座標 & 移除蛇尾 node 並在頭部新增新的蛇頭 node
        Node newHead=new Node(snakeX,snakeY);

        // 如果蛇有吃到果實，則不移除原本的尾巴 node
        if(snake.getSnakeBody().get(0).x==fruit.getX() && snake.getSnakeBody().get(0).y==fruit.getY()){
            // 重新設置 fruit 的位置 + 畫出新的 fruit + 分數加加
            fruit.setNewLocation(snake);
            fruit.drawFruit(g);
            score++;
        }else {
            snake.getSnakeBody().remove(snake.getSnakeBody().size()-1);
        }

        snake.getSnakeBody().add(0,newHead);

        // Requests that this Component gets the input focus
        requestFocusInWindow();

        // 全部東西都渲染好後，才讓玩家的 keypress 有效
        allowKeyPress=true;

    }

    @Override
    public Dimension getPreferredSize(){
        // 設定視窗大小 & 置中視窗
        return new Dimension(width,height);
    }

    public static void main(String[] args) {
        // 做出視窗 & 設定相關參數
        JFrame window=new JFrame("Snake game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Main());
        window.pack(); //
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false); // 固定大小，無法讓使用者調整
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(allowKeyPress){
            // 依據使用者按下的 arrow key & 目前蛇的方向，決定下一步蛇的方向該往哪裡
            if(e.getKeyCode()==37 && !direction.equals("Right")){
                direction="Left";
            } else if (e.getKeyCode()==38 && !direction.equals("Down")) {
                direction="Up";
            } else if (e.getKeyCode()==39 && !direction.equals("Left")) {
                direction="Right";
            } else if (e.getKeyCode()==40 && !direction.equals("Up")) {
                direction="Down";
            }

            // 更改 allowKeyPress，讓 allowKeyPress 再變回 true 前，玩家的 keypress 不會有反應
            allowKeyPress=false;
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void read_highest_score() {
        try {
            File myObj = new File(myFile);
            Scanner myReader = new Scanner(myObj);
            highest_score = myReader.nextInt();
            myReader.close();
        } catch (FileNotFoundException e) {
            highest_score = 0;
            try {
                File myObj = new File(myFile);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                }
                FileWriter myWriter = new FileWriter(myObj.getName());
                myWriter.write("" + 0);
                myWriter.close();
            } catch (IOException err) {
                System.out.println("An error occurred");
                err.printStackTrace();
            }
        }
    }

    public void write_a_file(int score) {
        try {
            if (score > highest_score) {
                FileWriter myWriter = new FileWriter(myFile);
                System.out.println("rewriting score...");
                myWriter.write("" + score);
                highest_score = score;
                myWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}