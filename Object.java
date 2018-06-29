import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;

class Point{

    private double x;
    private double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public Point translation(Point t){
        return new Point(this.x+t.getX(), this.y+t.getY());
    }

    public Point to2DIndex(){
        int newX = (this.x<0)? (((int)this.x)-1) : (int)this.x;
        int newY = (this.y<0)? (((int)this.y)) : (int)this.y;
        return new Point(newX, -newY);
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }
}

class Row{

    private Color[] elements;
    private Boolean[] status;
    private int size;

    public Row(int size){
        this.size = size;

        this.elements = new Color[size];
        this.status = new Boolean[size];
        for(int i = 0; i < size; i++){
            status[i] = false;
        }
    }
    public int size(){
        return this.size;
    }
    public Color get(int i){
        return this.elements[i];
    }
    public Boolean isOccupied(int i){
        return this.status[i];
    }
    public void setColor(int i, Color color){
        this.elements[i] = color;
        this.status[i] = true;
    }
    public Boolean allOccupied(){
        Boolean ret = true;
        for(int i = 0; i < this.size; i++){
            ret &= status[i];
        }
        return ret;
    }
}

class Tetromino{

    private ArrayList<Point> elements = new  ArrayList<Point>();

    private Point center;
    private Point translation;
    private double rotation;
    private Color color;
    private int type;

    private void TypeI(){
        this.center = new Point(5.0, -2.0);
        this.elements.add(new Point(3.5, -1.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
        this.elements.add(new Point(6.5, -1.5));
    }

    private void TypeJ(){
        this.center = new Point(4.5, -1.5);
        this.elements.add(new Point(3.5, -0.5));
        this.elements.add(new Point(3.5, -1.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
    }

    private void TypeL(){
        this.center = new Point(4.5, -1.5);
        this.elements.add(new Point(3.5, -1.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
        this.elements.add(new Point(5.5, -0.5));
    }

    private void TypeT(){
        this.center = new Point(4.5, -1.5);
        this.elements.add(new Point(3.5, -1.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
        this.elements.add(new Point(4.5, -0.5));
    }

    private void TypeS(){
        this.center = new Point(4.5, -1.5);
        this.elements.add(new Point(3.5, -1.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(4.5, -0.5));
        this.elements.add(new Point(5.5, -0.5));
    }

    private void TypeZ(){
        this.center = new Point(4.5, -1.5);
        this.elements.add(new Point(3.5, -0.5));
        this.elements.add(new Point(4.5, -0.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
    }

    private void TypeO(){
        this.center = new Point(5.0, -1.0);
        this.elements.add(new Point(4.5, -0.5));
        this.elements.add(new Point(5.5, -0.5));
        this.elements.add(new Point(4.5, -1.5));
        this.elements.add(new Point(5.5, -1.5));
    }

    public Tetromino(int type){
        this.translation = new Point(0.0, 0.0);
        this.rotation = 0;
        this.type = type;
        int rad1, rad2, rad3;
        do{
            rad1 = new Random().nextInt(256);
            rad2 = new Random().nextInt(256);
            rad3 = new Random().nextInt(256);
        }while(((rad1 < 90) && (rad2 < 90) && (rad3 < 90)) || ((rad1+rad2+rad3)<300));
        this.color = new Color(rad1, rad2, rad3);
        switch (type) {
            case 0://"I":
                TypeI();
                break;
            case 1://"J":
                TypeJ();
                break;
            case 2://"L":
                TypeL();
                break; 
            case 3://"T":
                TypeT();
                break;
            case 4://"Z":
                TypeS();
                break;
            case 5://"S":
                TypeZ();
                break;   
            case 6://"O":
                TypeO();
                break;
        
            default:
                System.out.print("Unknown type, ignore\n");
                break;
        }
    }

    @Override
    public String toString(){
        String temp = "";
        for(int i = 0; i<this.elements.size();i++){
            System.out.println(this.elements.get(i).to2DIndex().toString());
            temp.concat(this.elements.get(i).to2DIndex().toString());
        }
        return temp;
    }

    public void addTranslation(double x, double y){
        this.translation = this.translation.translation(new Point(x, y));
    }
    public void addTranslation(Point t){
        this.translation = this.translation.translation(t);
    }

    public void addRotation(double rad){

        this.rotation += rad;

        ArrayList<Point> newElements = new ArrayList<Point>();
        for(int i = 0; i < this.elements.size();i++){
            Point old = this.elements.get(i);
            double x = old.getX() - center.getX();
            double y = old.getY() - center.getY();
            double coor = (x < 0)? 1 : 0;
            double newX = Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) * Math.cos(Math.atan(y/x)+coor*Math.PI+rad);
            double newY = Math.sqrt(Math.pow(x,2) + Math.pow(y,2)) * Math.sin(Math.atan(y/x)+coor*Math.PI+rad);       
            newElements.add(new Point(Math.round(newX*10.0)/10.0+center.getX(), Math.round(newY*10.0)/10.0+center.getY()));
        }
        this.elements = newElements;
    }

    public ArrayList<Point> getElements(){
        ArrayList<Point> temp = new ArrayList<Point>();
        for(int i = 0; i<this.elements.size();i++){
            temp.add(this.elements.get(i).translation(this.translation).to2DIndex());
        }
        return temp;
    }
    public Point getCenter(){
        return this.center.translation(this.translation).to2DIndex();
    }

    public void setColor(Color color){
        this.color = color;
    }
    public Color getColor(){
        return this.color;
    }
    
    public Point getTranslation(){
        return this.translation;
    }

    public double getRotation(){
        return this.rotation;
    }

    public int getType(){
        return this.type;
    }

    public void printArray(){
        for(int i = 0; i < this.elements.size();i++){
            System.out.println(this.elements.get(i).to2DIndex());
        }
    }
    public void test(ArrayList<Point> pList){
        for(int i = 0; i < pList.size();i++){
            System.out.println(pList.get(i));
        }
    }
}
