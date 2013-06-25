
/**
 * Row.java
 */

package controlcenter;
import static controlcenter.ControlCenter.*;
import helpclasses.MenuItemGroup;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import user.Guest;

/**
 * Klassen beskriver en rad ur programmets interna tabell.
 * Klassen används för att skapa en tabell som ger en översiktsbild över
 * användarens kortsamlingar. <br>
 * Varje rad består av kortsamlingens namn, antal utgångna kort samt två
 * knappar. En knapp för att radera en kortsamling och en annan knapp för att
 * öppna en kortsamling. Knappvalet skickas genom en referens till
 * <code>ControlCenter</code> som i sin öppnar kortsamligen
 * eller så skickas anropet vidare till <code>DBController</code> som raderar
 * kortsamlingen.
 * @author Kristian Mörling
 */
public class Row extends JPanel {
    private String tableName;
    private ControlCenter ctrlCenter;
    
    public Row(String tableName, ControlCenter ctrlCenter) {
        this.tableName = tableName;
        this.ctrlCenter = ctrlCenter;
        
        setPreferredSize(new Dimension(400, 25)); //storlek på varje rad
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        //initiering av komponenter
        JLabel[] labels = new JLabel[2]; //label:s för namn och utgångna kort
        JButton[] btns = { new JButton("Öppna"), new JButton("Radera") };
        int nbrOfOutdatedCards = user. //hämta antal utgångna kort
                getCardCollection(tableName).getOutdatedCount();
        
        //varje tabell skrivs på formen "tabellnamn_användarid",
        //genom substring så väljer man att endast visa tabellnamn
        labels[0] = new JLabel(tableName.substring(0, tableName.length() - 5));
        labels[1] = new JLabel(""+nbrOfOutdatedCards, JLabel.CENTER);
        
        //sätt tooltip text
        labels[0].setToolTipText(labels[0].getText());
        labels[1].setToolTipText(labels[1].getText());
        
        //storlek på label:s och lägg till label:s
        Dimension lblSize = new Dimension(100, 20);
        for (int i = 0; i < 2; i++) {
            labels[i].setPreferredSize(lblSize);
            labels[i].setMaximumSize(lblSize);
            labels[i].setMinimumSize(lblSize);
            add(labels[i]);
        }
        
        //inställningar och utseende, knappar
        Dimension btnSize = new Dimension(80, 20);
        for (int i = 0; i < 2; i++) {
            btns[i].setPreferredSize(btnSize);
            btns[i].setMaximumSize(btnSize);
            btns[i].setMinimumSize(btnSize);
            btns[i].addActionListener(new AL());
            btns[i].setBorder(BorderFactory.createRaisedBevelBorder());
            //knappar blir endast aktiva när fönster för tabell visas
            MenuItemGroup.addMember(btns[i], MenuItemGroup.CTRL_CENTER);
        }
        if (user instanceof Guest) { //gäst konto ska inte kunna radera tabeller
            btns[1].setEnabled(false);
        }
        
        //lägg till komponenter
        add(Box.createRigidArea(new Dimension(35, 20))); //avstånd mellan knappar och text
        add(btns[0]);
        add(Box.createRigidArea(new Dimension(5, 20))); //avstånd mellan knappar
        add(btns[1]);
    }
    
    /**
     * Lyssnarklass för knapptryck.
     */
    private class AL implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            switch (ae.getActionCommand()) {
                case "Öppna": //öppnar tabell
                    ctrlCenter.showPreparePane(tableName);
                    break;
                case "Radera": //öppnar dialogfönster för att ta bort tabell
                    ctrlCenter.getDBController().removeCollection(tableName);
            }
        }
    }
}
