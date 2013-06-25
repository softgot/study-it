
/**
 * SSConfigInput.java
 */

package studysession;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lyssnarklass för konfigurationsfönstret. Konfigurationsfönstret används för 
 * att ställa in tidsbegränsning och ordning på kortsamling inför en repetition 
 * av en kortsamling.
 * Klassen lyssnar efter knapptryck på knappen "Studera" som anropar 
 * <code>SSController</code>-klassen för att starta repetition av kortsamling.
 * @author Kristian Mörling
 */
public class SSConfigInput implements ActionListener {
    private SSController controller;
    
    /**
     * Konstruktören tar emot en referens till den klass som ska anropas när man 
     * trycker på knappen "Studera".
     * @param controller referens till klass att anropa vid knapptryck
     */
    public SSConfigInput(SSController controller) {
        this.controller = controller;
    }
    
    /**
     * Lyssnar efter tryck på knappen "Studera".
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Studera")) {
            controller.showStudySession();
        }
    }

}
