import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import javafx.scene.transform.Rotate;

import java.awt.Image;
import java.awt.image.BufferedImage;

class Windows extends JPanel {

    private Boolean gameOver = false;

    private Board board;    
    private int xBase;
    private int yBase;
    private int blockLen;

    private BufferedImage initBuffer;

    public Windows(Board board, int width, int height){
        this.board = board;
        this.xBase = 200;
        this.yBase = 38;
        this.blockLen = 32;
        this.initBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = this.initBuffer.getGraphics();
        g.setColor(new Color(240, 240, 240));
        g.drawRect(0, 0, width, height);
        this.drawDecoration(g);
    }

    @Override
    public void paintComponent(Graphics g)
    {   
        //this.recList = this.board.getRecList();
        //this.gameOver = this.board.isGameOver();
        //super.paintComponent(g);
        //setBackground(new Color(255, 255, 192));

        setBackground(new Color(240, 240, 240));
        g.fillRect(200, 0, 320, 710);
        g.drawRect(200, 0, 320, 710);
        this.drawTatromino(g);
        this.drawDecoration(g);
        this.drawScore(g);
    }
    private void drawScore(Graphics g){
        ScoringSystem score = this.board.getScore();
        if(score == null){
            return;
        }
        String winnerInfo = "[" + score.getWinnerName() + "]    " + String.valueOf(score.getWinnerScore());
        String playerInfo = "[" + score.getPlayerName() + "]    " + String.valueOf(score.getPlayerScore());
        

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        g.drawString(winnerInfo, 560, 249);
        g.drawString(playerInfo, 560, 349);
    }

    private void drawDecoration(Graphics g){

        g.setColor(new Color(255, 255, 255));

        g.fillRoundRect(150, 0, 420, 70, 30, 30);
        g.setColor(Color.BLACK);
        g.drawRoundRect(150, 0, 420, 70, 30, 30);
        g.drawRect(0, 710, this.getWidth(), this.getHeight()-710);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD+Font.ITALIC, 50));
        g.setColor(new Color(30, 30, 30));
        g.drawString("Tetris", 280, 55);

        //g.setColor(new Color(20,20,20));
        //g.fillRoundRect(10, 200, 160, 200, 20, 20);
        //g.setColor(Color.WHITE);
        //g.drawRoundRect(10, 200, 160, 200, 20, 20);

        int hintLeftBoard = 12;
        g.setColor(Color.BLACK);
        g.fillRoundRect(hintLeftBoard, 200, 160, 200, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(hintLeftBoard, 200, 160, 200, 20, 20);

        g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 20));
        g.drawString("Next: ", hintLeftBoard+8, 230);

        try{
            Tetromino nextTetromino = this.board.getNextTetromino();
            ArrayList<Point> pList = nextTetromino.getElements();
            Color curColor = nextTetromino.getColor();
            for(int i = 0; i<pList.size(); i++){
                Point cur = pList.get(i);
                int nextYBase = 270;
                int x = (int)(cur.getX()-3) * blockLen + (hintLeftBoard + 25);
                int y = (int)(cur.getY()) * blockLen + (nextYBase);
                g.setColor(curColor);
                g.fillRoundRect(x, y, blockLen, blockLen, 13, 13);
                g.setColor(Color.BLACK);
                g.drawRoundRect(x, y, blockLen, blockLen, 13, 13);           
            }
        }catch(Exception ex){

        }

        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        g.drawString("Highest Score: ", 550, 200);
        g.drawString("Current Score: ", 550, 300);
        g.setColor(Color.WHITE);
        g.fillRoundRect(540, 220, 200, 40, 20, 20); 
        g.fillRoundRect(540, 320, 200, 40, 20, 20); 
        g.setColor(Color.BLACK);
        g.drawRoundRect(540, 220, 200, 40, 20, 20); 
        g.drawRoundRect(540, 320, 200, 40, 20, 20);  

    }

    private void drawTatromino(Graphics g){

        try{
            Tetromino curTetromino = this.board.getCurTetromino();
            ArrayList<Point> pList = curTetromino.getElements();
            Color curColor = curTetromino.getColor();
            for(int i = 0; i<pList.size(); i++){
                Point cur = pList.get(i);
                int x = (int)cur.getX() * blockLen + xBase;
                int y = (int)cur.getY() * blockLen + yBase;
                g.setColor(curColor);
                g.fillRoundRect(x, y, blockLen, blockLen, 13, 13);
                g.setColor(Color.BLACK);
                g.drawRoundRect(x, y, blockLen, blockLen, 13, 13);           
            }
        }catch(Exception ex){

        }      

        try{
            ArrayList<Row> fixed = this.board.getFixed();
            int max = fixed.size()-1;
            for(int i = max; i>=0; i--){
                Row curRow = fixed.get(i);
                int y = yBase + (max-i)*blockLen;
                for(int j = 0; j < curRow.size(); j++){
                    if(curRow.isOccupied(j) == false){
                        continue;
                    }
                    int x = xBase + j*blockLen;
                    //System.out.println(new Point(j, i));
                    g.setColor(curRow.get(j));
                    g.fillRoundRect(x, y, blockLen, blockLen, 13, 13);
                    g.setColor(Color.BLACK);
                    g.drawRoundRect(x, y, blockLen, blockLen, 13, 13);  
                }
            }

        }catch(Exception ex){

        }

    }
}

public class MainField extends JFrame {

    private Windows field;
    private Board parent;

    public MainField(Board parent){
        this.parent = parent;
        this.field = new Windows(parent, 780, 800);
        //this.field.setPreferredSize(new Dimension(540, 720));
        this.setContentPane(field);
        this.setBounds(300, 0, 780, 800);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addKeyListener(new fieldListener(this.parent));

        this.setVisible(true);
        this.repaint();
    }

    public void updateUI(){
        this.repaint();
    }

}
