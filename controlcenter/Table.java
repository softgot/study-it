
/**
 * Table.java
 */

package controlcenter;
import static controlcenter.ControlCenter.user;
import helpclasses.MenuItemGroup;
import java.awt.*;
import java.text.Collator;
import java.util.*;
import javax.swing.*;

/**
 * Klassen beskriver programmets interna tabell.
 * Tabellen ger en översiktsbild över användarens kortsamlingar samt antalet kort
 * i tabellen som gått ut.
 * @author Kristian Mörling
 */
public class Table extends JPanel {
    private ControlCenter ctrlCenter;
    
    public Table(ControlCenter ctrlCenter) {
        this.ctrlCenter = ctrlCenter; //knapptryck anropar detta objekt
        setTable();
    }
    
    private JScrollPane createTable() {
        Row[] rows; //fält för tabellens rader
        ArrayList<String> userTables;
        JPanel rowsPanel = new JPanel(null); //panel för samtliga rader
        Font font = new Font("Serif", Font.BOLD, 15);
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        userTables = user.getUserCollections(); //hämta kortsamlingar

        JLabel lblCollections = new JLabel("Samlingar", JLabel.LEFT);
        JLabel lblNbrOutdated = new JLabel("Utgångna kort", JLabel.CENTER);
        
        //sortera samlingar i alfabetisk ordning
        Collections.sort(userTables, new Comparator<String>() {
            Collator col = Collator.getInstance(); //för korrektare jämförelse 
            { col.setStrength(Collator.PRIMARY); } //noggrannhet vid jämförelse
            
            public int compare(String s1, String s2) {
                return col.compare(s1, s2); //jämför strängar
            }
        }); 
        
        //Höjden ändras dynamiskt utefter antalet rader. Varje rad är 25px hög.
        //52 är summan av mellanrum mellan tabell och ram samt höjd på rubriktext.
        rowsPanel.setPreferredSize(new Dimension(450, (25 * userTables.size() + 52) ));
        scrollPane.setPreferredSize(new Dimension(500, 385));
        scrollPane.setBorder(BorderFactory.createRaisedBevelBorder());
        
        //scrollPane scrollhastighet
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        //rubriktext
        lblCollections.setFont(font);
        lblNbrOutdated.setFont(font);
        lblCollections.setBounds(40, 12, 80, 20);
        lblNbrOutdated.setBounds(136, 12, 120, 20);
        rowsPanel.add(lblCollections); //lägg till rubriktext
        rowsPanel.add(lblNbrOutdated); //i panel för tabell
        
        //fyll tabell med kortsamlingar
        String tableName = "";
        if (userTables != null) { //om det finns kortsamlingar, lägg till dem
            rows = new Row[ userTables.size() ]; //antal samlingar = antal rader
            for (int i = 1; i <= userTables.size(); i++) {
                tableName = userTables.get(i - 1);
                rows[i - 1] = new Row(tableName, ctrlCenter); //skapa rad
                
                //varje rad upptar i höjdled 25px + förskjutning på 10px
                rows[i - 1].setBounds(45, i * 25 + 10, 500, 20);
                rowsPanel.add(rows[i - 1]); //lägg till i tabellpanel
            }
        }
        return scrollPane;
    }
    
    /**
     * Metoden används för att uppdatera tabellen.
     * Vad som händer vid anrop är att ifall någon tabell redan existerar
     * så raderas den och en ny tabell skapas istället. Eftersom raderna
     * i en tabell innehåller knappar som är medlemmar i knappgruppen
     * <code>CTRL_CENTER</code>, så avregistreras dessa knappar som medlemmar
     * från knappgruppen. <br><br>
     *
     * Därför att när man skapar en ny tabell så skapas det nya knappobjekt. I
     * knappgruppen ersätts de gamla knapparna, på det sättet att de gamla knapparna
     * avregistreras från knappgruppen och de nya knapparna registrerar sig som
     * nya medlemmar i knappgruppen.
     */
    public void setTable() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //ta bort gamla knappar från knappgrupp
                MenuItemGroup.removeTableButtons();
                removeAll(); //ta bort tabell
                
                add(createTable()); //lägg till ny tabell
                revalidate(); //uppdatera fönster
            }
        });
    }
}
