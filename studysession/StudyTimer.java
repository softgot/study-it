
/**
 * StudyTimer.java
 **/

package studysession;
import static controlcenter.ControlCenter.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;
import static main.Main.*;

/**
 * Klassen beskriver en timer som används vid övning av en kortsamling.
 * Timern använder en tråd som är igång så länge inte tiden gått och så
 * länge som inte användaren avbrutit en kortövning.
 * @author Kristian Mörling
 */
public class StudyTimer implements Runnable {
    private Thread thread; 
    private Date date = new Date();   
    private DateFormat df = new SimpleDateFormat("mm:ss");
    private long timeLimit, startTime, timeLeft, timeElapsed;
    
    /**
     * Konstruktören tar emot tid och startar timer.
     * Konstruktören tar emot den tidsbegränsning användaren valt. Därefter 
     * startas timern.
     * @param timeLimit tid timer ska ticka ner angivet i millisekunder
     */
    public StudyTimer(long timeLimit) {
        this.timeLimit = timeLimit;
        timeLeft = timeLimit;
        start();
    }
    
    /**
     * Metoden som startar timer.
     * Anropas av konstruktör.
     */
    private void start() {
        thread = new Thread(this); 
        startTime = System.currentTimeMillis(); //spara starttid
        thread.start(); //starta timer
    }
    
    /**
     * När timern inaktiverats, anropas metoden som tar bort texten som visar 
     * återstående tid.
     */
    private void stop() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lblTimer.setText(""); //klockan stoppad, ta bort tid
            }
        });
    }
    
    /**
     * Anropas varje sekund för att ticka ner tiden.
     * @return true = om det finns mer tid över, false = ifall tiden gått ut
     */
    private boolean ticDown() {
        timeElapsed = System.currentTimeMillis() - startTime; //förfluten tid
        timeLeft = timeLimit - timeElapsed; //tid över
        date.setTime(timeLeft); //formatera återstående tid till mm:ss 
        return timeLeft > 0;
    }
    
    /**
     * Kontrollerar ifall tiden gått ut.
     * Anropas från <code>StudySession</code>. 
     * @return true = tid gått ut, false = tid har ej gått ut
     */
    public boolean isTimeOver() {
        return timeLeft < 1;
    }
    
    /**
     * Trådmetod tickar ned klockan 1 sekund åt gången.
     */
    public void run() {
        
        //ticka ner tiden så länge tid finns över och kortövning ej avslutats
        while ( ticDown() && sessionActive ) {
            
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        //visa återstående tid i programmets södra panel
                        lblTimer.setText(df.format(timeLeft)); 
                    }
                });
                thread.sleep(1000); //klockan tickar ned 1 sekund åt gången
            } catch (InterruptedException ex) {
                System.out.println(ex);
                timeLeft = 0; //avsluta övning ifall timer slutar fungera
            }
            
        }
        stop(); //raderar text som visar klocka
    }
}