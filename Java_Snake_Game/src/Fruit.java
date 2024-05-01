import java.awt.*;
import java.util.ArrayList;

public class Fruit {
    private int x;
    private int y;

    public Fruit(){
        // 讓水果每次都出現在隨機的位置上
        this.x=(int)(Math.floor(Math.random()*Main.column)*Main.CELLSIZE);
        this.y=(int)(Math.floor(Math.random()*Main.row)*Main.CELLSIZE);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public void drawFruit(Graphics g){
        g.setColor(Color.RED);
        g.fillOval(this.x,this.y,Main.CELLSIZE,Main.CELLSIZE);
    }

    // 畫新的 fruit 位置，input 用蛇的位置，避免 fruit 出現在蛇身上
    public void setNewLocation(Snake s){
        int new_x;
        int new_y;
        boolean overlapping;
        do{
            new_x=(int)(Math.floor(Math.random()*Main.column)*Main.CELLSIZE);
            new_y=(int)(Math.floor(Math.random()*Main.row)*Main.CELLSIZE);
            overlapping=check_overlap(new_x,new_y,s);
        }while (overlapping);

        this.x=new_x;
        this.y=new_y;
    }

    private boolean check_overlap(int x,int y,Snake s){
        ArrayList<Node> snake_body=s.getSnakeBody();
        for(int j=0;j<s.getSnakeBody().size();j++){
            if(x==snake_body.get(j).x && y==snake_body.get(j).y){
                return true;
            }
        }
        return  false;
    }
}
