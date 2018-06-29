import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.SwingUtilities;

import java.util.Random;
import java.awt.*;

class Board{
    
    private ArrayList<Row> rowList;    // inner ArrayList represents each row reverse up
    private Semaphore updateLock = new Semaphore(1);
    private Semaphore pauseLock = new Semaphore(2);
    private Tetromino curTetromino;
    private Tetromino nextTetromino;
    private Thread updateThread;
    private Thread fallThread;
    private ArrayList<Thread> threadList;
    private Boolean gameOver = false;
    private int dimCol;
    private int dimRow;
    private MainField field;
    private ScoringSystem score;
    private Point failPoint;
    private Menu menu;

    public Board(int col, int row){

        this.field = new MainField(this);

        this.dimCol = col;
        this.dimRow = row;

        this.menu = new Menu(this.field, "Welcome Tetris", "Start");
        this.score = new ScoringSystem(this.menu.getName());

        resetAll();

        new Counter(this.field, 3);
    }    
    private void resetAll(){

        rowList = new ArrayList<Row>();
        for(int i = 0; i<dimRow; i++){
            this.rowList.add(new Row(dimCol));
        }

        this.nextTetromino = null;
        this.curTetromino = null;

        threadList = new ArrayList<Thread>();

        this.updateThread = new Thread(new Runnable(){
            @Override
            public void run() {
                Boolean done = false;
                while(done == false){
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException ex){
                        if(gameOver == true){
                            done = true;
                        }else{
                            try {
                                pauseLock.acquire();
                            } catch (Exception e) {
                                //TODO: handle exception
                            }
                            pauseLock.release();
                        }
                    }
                    if(done == false)
                    {
                        field.updateUI();
                    }       
                }
                System.out.println("Update exit");
            }
        });
        this.threadList.add(this.updateThread);

        this.fallThread = new Thread(new Runnable(){
        
            @Override
            public void run() {

                createNewTetromino();
                Boolean done = false;
                while(done == false){
                    try{Thread.sleep(500);}catch(InterruptedException ex){
                        try {
                            pauseLock.acquire();
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                        pauseLock.release();
                    }
                    
                    try{updateLock.acquire();}catch(InterruptedException ex){
                        try {
                            pauseLock.acquire();
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                        pauseLock.release();
                    }
                    if(done == true){
                        break;
                    }
                    if(tryMove(0, -1) == false){
                        if(updateFixed() == false)
                        {
                            gameOver = true;
                            updateThread.interrupt();
                            done = true;
                        }
                        updateLock.release();
                    }else{
                        updateLock.release();
                    }
                }
                System.out.println("Fall exit");
                gameOver();
            }
        });
        this.threadList.add(this.fallThread);
    }
    private void gameOver(){
        
        score.updateLogFile();
        this.menu = new Menu(this.field, "Game Over", "Restart");
        this.score = new ScoringSystem(this.menu.getName());
        System.out.println("restart");
        gameOver = false;
        resetAll();
        this.field.updateUI();
        new Counter(this.field, 3);

        gameStart();
    }
    public void callMenu(){
        
        try {
            this.pauseLock.acquire(2);
        } catch (Exception e) {
            System.out.println("call menu acquire fail");
        }
        for(int i = 0; i < this.threadList.size(); i++){
            this.threadList.get(i).interrupt();;
        }
        this.menu = new Menu(this.field,"Game Paused", "Resume");

        new Counter(this.field, 3);
        this.pauseLock.release(2); 
        
    }

    private Boolean createNewTetromino(){
        curTetromino = nextTetromino;
        int type = new Random().nextInt(7);
        nextTetromino = new Tetromino(type);
        if (isValid() == false) {
            return false;
        }
        return true;
    }
    private Boolean updateFixed(){   
        ArrayList<Point> pList = curTetromino.getElements();
        for(int i = 0; i<pList.size(); i++){
            Point curPoint = pList.get(i);
            int x = (int)curPoint.getX();
            int y = (int)curPoint.getY();
            rowList.get(dimRow-1-y).setColor(x, curTetromino.getColor());
        }
        int dismissCount = 0;     
        for(int i = dimRow -1; i>=0;i--){
            if(rowList.get(i).allOccupied() == true){
                rowList.remove(i);
                dismissCount += 1;
                rowList.add(new Row(dimCol));
            }
        }
        if(dismissCount > 0){
            score.updatePlayerScore(100*(int)Math.pow(2, dismissCount-1));
        }else{
            score.updatePlayerScore(0);
        }
        return createNewTetromino();
    }
    private Boolean isValid(){
        ArrayList<Point> pList = curTetromino.getElements();
        for(int i = 0; i<pList.size(); i++){
            Point curPoint = pList.get(i);
            int x = (int)curPoint.getX();
            int y = (int)curPoint.getY();

            failPoint = curPoint;
            if(x < 0 || x >= dimCol){
                return false;
            }
            if(y < 0 || y >= dimRow){
                return false;
            }

            if(rowList.get(dimRow-1-y).isOccupied(x) == true){
                return false;
            }
        }
        return true;
    }

    private Boolean tryMove(int x, int y){
        try {
            curTetromino.addTranslation(x, y);
        } catch (Exception e) {
            return false;
        }
        if(isValid() == false)
        {
            curTetromino.addTranslation(-x, -y);
            return false;
        }
        return true;
    }
    private Boolean tryRotate(double rad){
        try{
            curTetromino.addRotation(rad);
        }catch(Exception ex){
            return false;
        }
        if(isValid() == false)
        {
            Point center = curTetromino.getCenter().to2DIndex();

            int dx = (int) (center.getX() - failPoint.getX());
            int dy = (int) (failPoint.getY() - center.getY());
            try{dx = dx/Math.abs(dx);}catch(Exception ex){dx = 0;}
            try{dy = dy/Math.abs(dy);}catch(Exception ex){dy = 0;}

            for(int i = 1; i<3; i++){
                if(tryMove(i*dx, 0) == true){
                    return true;
                }
            }
            for(int i = 1; i<3; i++){
                if(tryMove(0, i*dy) == true){
                    return true;
                }
            }

            for(int i = 1; i<3; i++){
                for(int j = 1; j<3; j++){
                    if(tryMove(i*dx, j*dy) == true){
                        return true;
                    }
                }
            }

            curTetromino.addRotation(-rad);
            return false;
        }
        return true;
    }
    private Boolean trySwitch(){

        Point curTranslation = curTetromino.getTranslation();
        int curType = curTetromino.getType();
        Color curColor = curTetromino.getColor();
        int nextType = nextTetromino.getType();
        Color nextColor = nextTetromino.getColor();

        Tetromino temp = curTetromino;
        curTetromino = new Tetromino(nextType);
        curTetromino.addTranslation(curTranslation);
        curTetromino.setColor(nextColor);

        if(isValid() == false){
            curTetromino = temp;
            return false;
        }

        nextTetromino = new Tetromino(curType);
        nextTetromino.setColor(curColor);

        return true;
    }

    public void currentTransform(String type){
        try{updateLock.acquire();}catch(InterruptedException ex){}

        if(type == "up" || type == "counter_clockwise"){
            tryRotate(Math.PI/2.0);
        }
        if(type == "clockwise"){
            tryRotate(Math.PI/2.0);
        }
        if(type == "switch"){
            if(trySwitch() == true){
                score.ratePenalty(0.5);
            }
        }
        if(type == "left"){
            tryMove(-1, 0);
        }
        if(type == "right"){
            tryMove(1, 0);
        }
        if(type == "down"){
            if(tryMove(0, -1) == true){
                score.rateBonus(0.1);
            }
        }
        if(type == "done"){
            while(tryMove(0, -1) == true){
                score.rateBonus(0.2);
            }
        }
        updateLock.release();
    }

    public ArrayList<Row> getFixed(){
        return this.rowList;
    }
    public Tetromino getCurTetromino(){
        return this.curTetromino;
    }
    public Tetromino getNextTetromino(){
        return this.nextTetromino;
    }
    public ScoringSystem getScore(){
        return this.score;
    }

    public Boolean isGameOver(){
        return this.gameOver;
    }

    public void gameStart(){
        int type = new Random().nextInt(7);
        this.nextTetromino = new Tetromino(type);
        for(int i = 0; i < this.threadList.size(); i++){
            this.threadList.get(i).start();
        }
    }

    public static void main(String []args){

        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                new Board(10, 21).gameStart();
            }
        });

        //Board b = new Board(10, 21);
        
        //try{Thread.sleep(5000);}catch(Exception e){}

        //b.gameStart();
        //Tetromino a = new Tetromino(3);
        //System.out.println(a);
    }
}
