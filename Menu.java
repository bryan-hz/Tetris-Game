import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Menu {
    private String message = "Welcome Tetris";
    private JDialog menu;
    private JPanel mainPanel;
    private JFrame parent;
    private JLabel title;
    private JButton start;
    private JButton help;
    private JButton exit;
    private Boolean helpMode = false;
    private Boolean inputMode = false;
    private Boolean backMode = false;
    private JLabel helpMenu;
    private JLabel nameLabel; 
    private JTextField nameField;
    private String playerName = "";

    public Menu(JFrame f, String titleString, String buttonString) {

        this.message = titleString;

        this.parent = f;
        try{
            this.helpMenu = new JLabel(new ImageIcon(ImageIO.read(new File("helpMenu.png"))));
            this.helpMenu.setBounds(0, 10, 300, 400);
        }catch(Exception ex){
            this.helpMenu = new JLabel("Unable to find 'helpMenu.png'");
            this.helpMenu.setForeground(Color.WHITE);
            this.helpMenu.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC+Font.BOLD, 15));
            this.helpMenu.setBounds(35, 100, 400, 50);
        }
        this.nameLabel = new JLabel("Please insert your name:");
        this.nameLabel.setForeground(Color.WHITE);
        this.nameLabel.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC, 15));
        this.nameLabel.setBounds(40, 100, 200, 40);
        this.nameField = new JTextField();
        this.nameField.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC, 20));
        this.nameField.setBounds(40, 140, 220, 35);
        this.nameField.addKeyListener(new KeyListener(){
        
            @Override
            public void keyTyped(KeyEvent e) {
                //System.out.println("type: " + e.getKeyCode());
            }
        
            private Boolean pressed = false;
            @Override
            public void keyReleased(KeyEvent e) {
                this.pressed = false;
                //System.out.println("release: " + e.getKeyCode());
            }
        
            @Override
            public void keyPressed(KeyEvent e) {
                if(this.pressed == false)
                {
                    //System.out.println("Press: " + e.getKeyCode());
                    this.pressed = true;
                    if(e.getKeyCode() == 10){
                        start.doClick();
                    }
                }
            }
        });

        mainMenu(buttonString);
    }

    private void mainMenu(String buttonString){
        this.menu = new JDialog(this.parent, "Menu", true);  

        this.mainPanel = new JPanel();
        this.mainPanel.setBackground(Color.BLACK);
        this.mainPanel.setLayout(null);

        this.start = new JButton(buttonString);
        this.start.setBounds(95, 120, 100, 40);
        this.start.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {

                if(start.getText() == "Resume"){
                    menu.setVisible(false);
                }

                if(inputMode == false){
                    mainPanel.remove(title);
                    mainPanel.remove(help);
                    
                    exit.setText("Back");
                    exit.setBounds(0, 0, 85, 35);

                    mainPanel.add(nameLabel);
                    mainPanel.add(nameField);
                    nameField.setText("");
                    nameField.requestFocusInWindow();
                    start.setBounds(110, 180, 80, 38);
                    start.setText("Done");
                    inputMode = true;
                    backMode = true;
                    menu.revalidate();
                    menu.repaint();
                }else{

                    playerName = nameField.getText();
                    if(playerName.length() == 0){
                        nameLabel.setText("Name cannot be empty...");
                        menu.revalidate();
                        menu.repaint();
                        return;
                    }
                    if(playerName.length() > 5){
                        nameLabel.setText("Maximum 5 characters...");
                        menu.revalidate();
                        menu.repaint();
                        return;
                    }
                    //System.out.println(playerName);

                    menu.setVisible(false);
                }
            }
        });

        this.help = new JButton("Help");
        this.help.setBounds(95, 160, 100, 40);
        this.help.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                menu.invalidate();
                helpMode = !helpMode;
                if(helpMode == true){
                    mainPanel.add(helpMenu);
                    help.setText("Back");
                    help.setBounds(0, 0, 100, 40);
                    mainPanel.remove(title);
                    mainPanel.remove(exit);
                    mainPanel.remove(start);
                }else{
                    mainPanel.remove(helpMenu);
                    help.setText("Help");
                    help.setBounds(95, 160, 100, 40);
                    mainPanel.add(title);
                    mainPanel.add(start);
                    mainPanel.add(exit);
                            
                    start.requestFocusInWindow();
                }
                menu.revalidate();
                menu.repaint();
            }
        });
        

        this.exit = new JButton("Exit");
        this.exit.setBounds(95, 200, 100, 40);
        this.exit.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                if(backMode == true){
                    mainPanel.remove(nameField);
                    mainPanel.remove(nameLabel);
                    mainPanel.add(title);
                    mainPanel.add(help);

                    exit.setBounds(95, 200, 100, 40);
                    exit.setText("Exit");
                    start.setBounds(95, 120, 100, 40);
                    start.setText("Start");

                    inputMode = false;
                    backMode = false;
        
                    start.requestFocusInWindow();
                    menu.revalidate();
                    menu.repaint();
                }else{
                    menu.setVisible(false);
                    System.exit(0);
                }
            }
        });
    
        this.title = new JLabel(message);
        this.title.setForeground(Color.WHITE);
        this.title.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC+Font.BOLD, 30));
        this.title.setBounds(35, 30, 400, 50);

        this.mainPanel.add(this.title);
        this.mainPanel.add(this.start);
        this.mainPanel.add(this.help);
        this.mainPanel.add(this.exit);        
        start.requestFocusInWindow();

        this.menu.add(this.mainPanel);
        //this.menu.add(this.mainPanel);
        // setLayout(null);
        Rectangle r = this.parent.getBounds();
        this.menu.setBounds((int)r.getCenterX()-180, (int)r.getCenterY()-200, 300, 400);
        this.menu.setVisible(true);
    }

    public String getName(){
        return this.playerName;
    }
}

class Counter {

    private JButton process;
    private int counter;
    private JDialog menu;

    public Counter(JFrame f, int i){
        this.counter = i;

        menu = new JDialog(f, "Count down", true);
        menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.BLACK);

        JLabel num = new JLabel(String.valueOf(i));
        num.setForeground(Color.WHITE);
        num.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 200));
        num.setBounds(80, 30, 200, 200);

        process = new JButton();
        process.setBounds(0, 0, 0, 0);
        process.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) {
                counter--;
                if(counter == 0){
                    menu.setVisible(false);
                }
                num.setText(String.valueOf(counter));
                menu.revalidate();
                menu.repaint();
            }
        });

        mainPanel.add(process);
        mainPanel.add(num);
        menu.add(mainPanel);

        Rectangle r = f.getBounds();
        menu.setBounds((int)r.getCenterX()-180, (int)r.getCenterY()-200, 300, 300);
        Thread t = new Thread(new Runnable(){
        
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        //TODO: handle exception
                    }
                    process.doClick();
                }
            }
        });
        t.start();
        menu.setVisible(true);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    public static void main(String []args){
        JFrame f = new JFrame();

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(380, 0, 780, 800); JPanel p = new JPanel();
        JLabel l = new JLabel("something here");
        p.add(l);
        f.add(p);
        f.setVisible(true);
        Counter c = new Counter(f, 3);
        System.out.println("start");
    }

}
