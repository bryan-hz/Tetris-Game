import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class fieldListener implements KeyListener{
    
    /* left 37
     * up 38
     * right 39
     * dowm 40
     */

    private Board parent;     
    public fieldListener(Board parent){
        this.parent = parent;
    }

    private Boolean pressed = false;

    public void keyTyped(KeyEvent e){
        //System.out.println("Key type: " + e.getKeyChar());
    }

    public void keyPressed(KeyEvent e){
        if(this.pressed == true)
            return;
        pressed = true;
        switch(e.getKeyCode()){
            case 27:
            {
                this.parent.callMenu();
                break;
            }
            case 37:
            {
                this.parent.currentTransform("left");
                break;
            }            
            case 38:
            {
                this.parent.currentTransform("up");
                break;
            }
            case 39:
            {
                this.parent.currentTransform("right");
                break;
            }
            case 40:
            {
                this.parent.currentTransform("down");
                break;
            }
            case 67:
            {
                this.parent.currentTransform("clockwise");
                break;
            }
            case 90:
            {
                this.parent.currentTransform("counter_clockwise");
                break;
            }
            case 88:
            {
                this.parent.currentTransform("switch");
                break;
            }
            case 32:
            {
                this.parent.currentTransform("done");
                break;
            }
            default:
                System.out.println("Key press: " + e.getKeyCode());
        }
            
    }

    public void keyReleased(KeyEvent e){
        pressed = false;
    }

}
