import java.io.*;

public class ScoringSystem {

    final private String fileName = "history.log";

    private String winnerName = "";
    private int winnerScore = 10000;
    private String playerName = "";
    private int playerScore = 0;
    private double rate = 1;

    private void newLogFile(){
        try {
            FileWriter f = new FileWriter(this.fileName);
            BufferedWriter buf = new BufferedWriter(f);

            buf.write(this.winnerName + "\n" + this.winnerScore);
            buf.close();
            
        } catch (Exception ex) {
            this.winnerName = this.playerName;
            this.winnerScore = this.playerScore;
        }
    }

    private void readLogFile(){
        try {

            FileReader f = new FileReader(this.fileName);
            BufferedReader buf = new BufferedReader(f);
          
            this.winnerName = buf.readLine();
            this.winnerScore = Integer.valueOf(buf.readLine());
            buf.close();
            //System.out.println("Found: " + this.winnerName + ": " + this.winnerScore);

        } catch (Exception ex) {
            //System.out.println("None found");
            this.winnerName = this.playerName;
            this.winnerScore = this.playerScore;
            this.newLogFile();
        }
    }

    public ScoringSystem(String playerName) {
        this.playerName = playerName;
        this.playerScore = 0;
        readLogFile();
    }

    public String getPlayerName(){
        return this.playerName;
    }
    public int getPlayerScore(){
        return this.playerScore;
    }
    public String getWinnerName(){
        return this.winnerName;
    }
    public int getWinnerScore(){
        return this.winnerScore;
    }

    public void rateBonus(double r){
        this.rate += r;
    }
    public void ratePenalty(double m){
        this.rate *= m;
    }

    public void updatePlayerScore(int score){
        this.playerScore += this.rate * score;
        this.rate = 0;
        if(this.playerScore >= this.winnerScore){
            this.winnerScore = this.playerScore;
            this.winnerName = this.playerName;
        }
    }
    public void updateLogFile(){
        newLogFile();
    }
}
