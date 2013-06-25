
/**
 * ButtonBar.java
 */

package main;
import controlcenter.ControlCenter;
import database.DBController;
import helpclasses.MenuItemGroup;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Klassen beskriver programmets östra knappanel.
 * Den översta knappen (plustecken) används för att lägga till kort. <br> Den 
 * mellersta knappen (kikare) används för att öppna fristående tabellfönster.
 * <br>Den nedersta knappen (retursymbol) används för att återgå till 
 * programmets huvudfönster.
 * @author Kristian Mörling
 */
public class ButtonBar extends JPanel {
private JButton btnAdd = new JButton(new ImageIcon(this.getClass().getResource("/pictures/plus.png")));//bild plustecken
    private JButton btnDB = new JButton(new ImageIcon(this.getClass().getResource("/pictures/browse.png"))); //bild kikare
    private JButton btnReturn = new JButton(new ImageIcon(this.getClass().getResource("/pictures/return.png"))); //bild retursymbol
    private ControlCenter ctrlCenter;
    
    public ButtonBar() {
        JPanel buttonBar = new JPanel();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); //layout för stående panel
        setPreferredSize(new Dimension(70, 35)); //panelens önskade storlek

        //ställ in text som visas när man placerar muspekare över knapp
        btnAdd.setToolTipText("Lägg till kort i denna kortsamling");
        btnDB.setToolTipText("Öppna databas");
        btnReturn.setToolTipText("Återgå till huvudmeny");
        //används för att identifiera knappar i klass som grupperar knappar
        btnReturn.setActionCommand("Återgå till huvudmeny"); 
        btnDB.setActionCommand("Öppna databas");
        
        //ställ in utseende på knappar
        setButtonPrefs(btnAdd);
        setButtonPrefs(btnDB);
        setButtonPrefs(btnReturn);
        
        //lägg till knappar i panel
        buttonBar.setLayout(new GridLayout(3, 1));
        buttonBar.add(btnAdd);
        buttonBar.add(btnDB);
        buttonBar.add(btnReturn);

        //centrera placering av knappar och bestäm storlek på knappanel
        Dimension size = new Dimension(25, 100);
        add(Box.createRigidArea(size)); //avstånd mel. knappanel och övre ram
        add(buttonBar); //lägg till knappanel
        size = new Dimension(40, 113); 
        add(Box.createRigidArea(size)); //avstånd mel. knappanel och nedre ram

        //koppla knappar till lyssnare
        btnAdd.addActionListener(new AL()); 
        btnDB.addActionListener(new AL()); 
        btnReturn.addActionListener(new AL()); 
        
        //registrera knappar till respektive knappgrupp
        MenuItemGroup.addMember(btnDB, MenuItemGroup.CTRL_CENTER);
        MenuItemGroup.addMember(btnAdd, MenuItemGroup.SS_CONFIG);
        MenuItemGroup.addMember(btnReturn, MenuItemGroup.SS_CONFIG);
        MenuItemGroup.addMember(btnReturn, MenuItemGroup.SS_VIEWER);
    }
    
    /**
     * Metoden ställer in utseende på knapp som skickas som argument.
     * @param button knapp att ställa in utseende på 
     */
    private void setButtonPrefs(JButton button) {
        button.setFocusPainted(false); //markera inte knapp vid tryck
        button.setContentAreaFilled(false); //genomskinlig bakgrund
        button.setBorderPainted(false); //knapp utan ram
    }
    
    /**
     * Metoden ställer in objekt till <code>ControlCenter</code>.
     * Objektet används av lyssnarklassen för att anropa andra klasser
     * i programmet vid knapptryck.
     * @param ctrlCenter <code>ControlCenter</code>-objekt
     */
    public void setCtrlCenter(ControlCenter ctrlCenter) {
        this.ctrlCenter = ctrlCenter;
    }
    
    /**
     * Lyssnarklass för knapptryck.
     */
    private class AL implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            DBController dbCtrler = ctrlCenter.getDBController();
            if (e.getSource() == btnAdd) {
                dbCtrler.newCard(); //lägg till nytt kort
            } else if (e.getSource() == btnDB) {
                dbCtrler.openTable(); //öppna fristående tabellfönster
            } else if (e.getSource() == btnReturn) {
                ctrlCenter.showControlCenter(); //återvänd till huvudfönster
            }
        }
    }
}
