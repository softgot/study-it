
/**
 * DBPopupMenu.java
 */

package database;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Klassen beskriver det popupfönster som dyker upp när man högerklickar 
 * i det fristående tabellfönstret.
 * Genom en referens till lyssnarklassen så skickas händelser i popupfönstret
 * vidare till lyssnarklassen <code>DBInput</code>.
 * @author Kristian Mörling
 */
public class DBPopupMenu extends JPopupMenu {
    
    private final int numberOfItems = 4;
     
    public DBPopupMenu(DBInput dbInput) {
        JMenuItem[] popupItems = new JMenuItem[numberOfItems];
        String[] popupText = { "Uppdatera", "Öppna", "Ta bort", "Ändra kunskapsnivå"  };
        String[] picPaths = { "/pictures/update.png", "/pictures/folder_open.png", "/pictures/trash.png",
            "/pictures/set_level.png" };
        
        //skapa menyalternativ
        for (int i = 0; i < numberOfItems; i++) {
            popupItems[ i ] = new JMenuItem( popupText[i], //initiera varje meny-
                    new ImageIcon(this.getClass().getResource(picPaths[i]) )); //alternativ med bild och text
            popupItems[ i ].addActionListener(dbInput); //koppla händelser till lyssnarklass
            add(popupItems[i]);
        }
        setBorder(BorderFactory.createRaisedBevelBorder());
    }
}
