import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private ArrayList<Node> snakeBody;

    // 把四格的蛇 node 加入 arrayList 中
    public Snake(){
        snakeBody=new ArrayList<>();
        snakeBody.add(new Node(80,0));
        snakeBody.add(new Node(60,0));
        snakeBody.add(new Node(40,0));
        snakeBody.add(new Node(20,0));
    }

    public ArrayList<Node> getSnakeBody(){
        return snakeBody;
    }

    // 畫出蛇的 method
    public void drawSnake(Graphics g){

        // 把蛇頭標示成黃色
        for (int i=0;i<snakeBody.size();i++){

            if(i==0){
                g.setColor(Color.orange);
            }else {
                g.setColor(Color.white);
            }

            // 讓蛇撞到牆壁後，可以從相反的另一邊穿牆而出
            Node n=snakeBody.get(i);

            if(n.x>=Main.width){
                n.x=0;
            }

            if(n.x<0){
                n.x=Main.width-Main.CELLSIZE;
            }

            if(n.y>=Main.height){
                n.y=0;
            }

            if(n.y<0){
                n.y=Main.height-Main.CELLSIZE;
            }

            g.fillOval(n.x,n.y,Main.CELLSIZE,Main.CELLSIZE);
        }
    }
}
