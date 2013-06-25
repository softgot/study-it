
/**
 * DBInput.java
 */

package database;
import helpclasses.MenuItemGroup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;



/**
 * Lyssnarklass för aktivitet i det fristående tabellfönstret.
 * Klassen lyssnar efter fönsterhändelser, knapptryck och musklick.<br><br>
 * Fönsterhändelser när det fristående tabellfönstret öppnas eller stängs ned.<br>
 * Knapptryck när användaren trycker på knapparna "Uppdatera", "Öppna" eller 
 * "Ta bort".<br>
 * Musklick när användaren högerklickar inuti tabellfönstret.<br>
 * @author Kristian Mörling
 */
public class DBInput extends AbstractAction implements ActionListener, MouseListener, WindowListener {
    private DBController dbController;
    
    public DBInput(DBController dbController) {
        this.dbController = dbController;
    }

    /*****************************ActionListener*****************************
     ***********************************************************************/
    
    /**
     * Metoden lyssnar efter knapptryck i tabellfönster.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand(); 
        
        if ( actionCommand.equals("Uppdatera") ) {
            dbController.updateTable();
        } else if ( actionCommand.equals("Öppna") ) {
            dbController.openCard();
        } else if ( actionCommand.equals("Ta bort") ) {
            dbController.removeCard();
        } else if ( actionCommand.equals("Ändra kunskapsnivå") ) {
            dbController.setCardLevel();
        }
    }

    /*****************************Window Adapter***************************
     **********************************************************************/
    
    /**
     * Metoden används för att hålla reda på när det fristående tabellfönster visas.
     * När det fristående tabellfönstret visas så inaktiveras större delen av knapparna 
     * i programmets huvudfönster. De knappar som inte inaktiveras är 
     * "avsluta", "om" och knapparna för att ändra look and feel.
     * @param e 
     */
    @Override
    public void windowActivated(WindowEvent e) {
        MenuItemGroup.disableAllItems(); 
    }

    /**
     * Metoden används för att hålla reda på när det fristående tabellfönster stängs ner.
     * Vid nedstängning av fönstret så återaktiveras de knappar som inaktiverades
     * när fönstret öppnades.
     * @param e fönsterhändelse
     */
    @Override
    public void windowClosing(WindowEvent e) {
        dbController.returnToCtrlCenter();
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    /******************************Mouse Listener****************************
     ***********************************************************************/
    
    /**
     * Kontrollerar när användaren högerklickat i tabellfönster.
     * Vid högerklick visas en popupmeny med funktionerna "uppdatera",
     * "öppna" och "ta bort".
     * @param e mushändelse
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) { 
            dbController.showPopupMenu(e.getX(), e.getY()); //visa popupemeny där 
                                                        //muspekaren är placerad
        }
    }
    
    /**
     * Kontrollerar när användaren högerklickat i tabellfönster.
     * Vid högerklick visas en popupmeny med funktionerna "uppdatera",
     * "öppna" och "ta bort".
     * @param e 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            dbController.showPopupMenu(e.getX(), e.getY());
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}

}
