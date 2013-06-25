
/**
 * SSInput.java
 */

package studysession;
import java.awt.event.*;
import menubar.Menubar;

/**
 * Lyssnarklass för kortövningsfönster.
 * Knapparna "Visa svar", "Rätt" och "Fel" anropar klassen.
 * @author Kristian Mörling
 */
public class SSInput implements ActionListener {
    private SSController controller; //knapptryck anropar denna klass
    
    /**
     * Konstruktören tar emot ett objekt av typen <code>SSController</code>.
     * Objektet används av lyssnarmetoden för att anropa metoder i objektet 
     * vid knapptryck.
     * @param controller objekt till klass som anropas vid knapptryck
     */
    public SSInput(SSController controller, Menubar menubar) {
        this.controller = controller; 
        menubar.setEditCardListener(this); //denna klass lyssnar efter redigera kort-menyvalet
    }
    
    /*
     * Lyssnarmetoden lyssnar efter knapphändelser.
     */
    public void actionPerformed(ActionEvent e) {
        
        String aCmd = e.getActionCommand();
        switch (aCmd) {
            case "Visa svar": //visa kortets baksida
                controller.showAnswer();
                break;
            case "Rätt": //meddela svar som korrekt, visa nästa kort
                controller.nextCard(true);
                break;
            case "Fel": //meddela svar som felaktigt, visa nästa kort
                controller.nextCard(false);
                break;
            case "Redigera kort": //anrop från meny "Redigera kort", redigera aktuellt kort
                controller.editCurrentCard();
        }
    }
}
