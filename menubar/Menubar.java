
/**
 * Menubar.java
 */

package menubar;
import database.DBController;
import helpclasses.MenuItemGroup;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import main.Main;
import studysession.SSInput;

/**
 * Klassen beskriver programmets menyrad.
 * Klassen innehåller även en privat klass som används för att lyssna 
 * efter menyval.
 * @author Kristian Mörling
 */
public class Menubar extends JMenuBar {
    private final JMenu[] menuHdrs;
    private final int nbrOfMenuHdrs = 5;
    private Font font = new Font("SansSerif", Font.PLAIN, 10);
    private DBController dbc;
    private final int FILE = 0, EDIT = 1, OPTIONS = 2, TOOLS = 3, HELP = 4;

    public Menubar() {
        new About(); //initialiserar "Om" meny
        
        /****************************Huvudmenyer******************************/
        menuHdrs = new JMenu[nbrOfMenuHdrs];
        String[] menuHdrsTxt = { "Fil", "Redigera", "Alternativ", "Verktyg", "Hjälp" };
        for (int i = 0; i < nbrOfMenuHdrs; i++) {
            menuHdrs[i] = new JMenu( menuHdrsTxt[i] );
            menuHdrs[i].setFont(font);
            menuHdrs[i].setBorder(new EmptyBorder(3, 3, 3, 3)); //förstora menyrad
            add( menuHdrs[i] );
        }
        
        /****************************Submenyer********************************/
        setFileMenu();
        setEditMenu();
        setOptionsMenu();
        setToolsMenu();
        setHelpMenu();
        /*********************************************************************/
        setVisible(true);
    }

    /**
     * Metoden skapar huvudmenyn "Fil".
     * Huvudmenyn "Fil" innehåller submenyerna "Ny samling", "Nytt kort" och 
     * "Avsluta".
     */
    private void setFileMenu() {
        JMenu menuNew = new JMenu("Ny");
        JMenuItem[] menuNewSub = new JMenuItem[3];
        String[] menuNewText = { "Ny Samling", "Nytt kort", "Avsluta" };
        
        //skapa submenyer
        for (int i = 0; i < 3; i++) {
            menuNewSub[i] = new JMenuItem(menuNewText[i]);
            menuNewSub[i].setFont(font);
            menuNewSub[i].addActionListener(new MenuClickListener());
        }
        
        //lägg till submenyer
        menuNew.setFont(font);
        menuNew.add(menuNewSub[0]); //lägg till "Ny samling"
        menuNew.add(menuNewSub[1]); //lägg till "Nytt kort"
        
        menuHdrs[FILE].add(menuNew); //lägg till "Ny"
        menuHdrs[FILE].add(menuNewSub[2]); //lägg till "Avsluta"
        
        //anmäl submenyer till grupp
        MenuItemGroup.addMember(menuNewSub[0], MenuItemGroup.CTRL_CENTER);
        MenuItemGroup.addMember(menuNewSub[1], MenuItemGroup.SS_CONFIG);
    }
    
    private void setEditMenu() {
        JMenuItem menuEditCard = new JMenuItem("Redigera kort");
        menuEditCard.setFont(font);
        MenuItemGroup.addMember(menuEditCard, MenuItemGroup.SS_VIEWER);
        menuHdrs[EDIT].add(menuEditCard);
    }
    
    /**
     * Metoden skapar huvudmenyn "Alternativ".
     * Huvudmenyn "Alternativ" innehåller submenyerna "Skapa användare", 
     * "Byt användare" och "Ta bort användare" samt menyalternativ för att byta
     * "Look and feel". Hur många menyalternativ som skapas för look and feel, 
     * beror på hur många Look and Feel:s finns tillgängliga i systemets 
     * javainstallation.
     */
    private void setOptionsMenu() {
        JMenu menuUsers = new JMenu("Användare");
        //submenyer "Lägg till", "Ta bort" och "Byt användare"
        JMenuItem[] menuUsersSub = new JMenuItem[3]; 
        String[] menuUsersTxt = { "Skapa användare", "Byt användare", 
            "Ta bort användare" };
        
        //skapa submenyer för "Användare"
        for (int i = 0; i < 3; i++) {
            menuUsersSub[i] = new JMenuItem( menuUsersTxt[ i ] );
            menuUsersSub[i].setFont( font );
            menuUsersSub[i].addActionListener(new MenuClickListener());
            //anmäl till knappgrupp och lägg till i huvudmeny
            MenuItemGroup.addMember(menuUsersSub[i], MenuItemGroup.CTRL_CENTER);
            menuUsers.add( menuUsersSub[i] );
        }
        //typsnitt "Användare" och "Utseende"
        menuUsers.setFont( font );
        //lägg till submenyer i huvudmeny
        menuHdrs[ OPTIONS ].add( menuUsers ); //lägg till "Användare"
    }
    
    /**
     * Metoden skapar huvudmenyn "Verktyg".
     * "Verktyg" har submenyerna "Administrera databas" och "Återställ databas".
     */
    private void setToolsMenu() {
        JMenu menuDB = new JMenu("Databas");
        JMenuItem[] menuDBSub = new JMenuItem[ 2 ]; //submenyer till Databas
        String[] menuDBTxt = { "Administrera databas", "Återställ databas" };

        //skapa och lägg till "Adm. datab." & "Åters. datab." i "Databas"
        //anmäl därefter submenyer till knappgruppen CTRL_CENTER
        for (int i = 0; i < 2; i++) { 
            menuDBSub[ i ] = new JMenuItem( menuDBTxt[ i ] );
            menuDBSub[ i ].setFont( font );
            MenuItemGroup.addMember(menuDBSub[i], MenuItemGroup.CTRL_CENTER);
            menuDBSub[ i ].addActionListener(new MenuClickListener());
            menuDB.add( menuDBSub[ i ] ); //lägg till i "Databas"
        }
        menuDB.setFont( font ); //typsnitt "Databas"
        //lägg till "Databas" i huvudmenyn "Verktyg"
        menuHdrs[ TOOLS ].add(menuDB); 
    }
    
    /**
     * Metoden skapar huvudmenyn "Hjälp".
     * Huvudmenyn "Hjälp" innehåller submenyn "Om".
     */
    private void setHelpMenu() {
        JMenuItem menuHelpAbout = new JMenuItem("Om");
        menuHelpAbout.addActionListener(new MenuClickListener());
        menuHelpAbout.setFont( font );
        menuHdrs[ HELP ].add(menuHelpAbout); //Lägg till "Om" i "Hjälp"
    }
    
    /*
     * Används för att initiera lyssnare för menyvalet redigera kort.
     * Lyssnaren kopplas till SSInput som lyssnar efter aktivitet i 
     * en studiesession
     */
    public void setEditCardListener(SSInput ssInput) {
        menuHdrs[EDIT].getItem(0).addActionListener(ssInput); //osäker på om rätt komponent i fält
    }
    
    public void setDbController(DBController dbCtrler) {
        this.dbc = dbCtrler;                
    }
    
    /**
     * Metoden öppnar dialogfönstret "Om" som visar namnet på programmet.
     */
    public void showAbout() {
        About.showAbout();
    }
    
    /**
     * Klassen lyssnar efter knapptryck i menyraden.
     * Anropen vidarebefordras till respektive klass genom referensen till 
     * <code>ControlCenter</code>.
     */
    private class MenuClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            String aCmd = e.getActionCommand(); //actionCommand
            
            //användaren tryckte inte på "look and feel" knapp,
            //kontrollera vilken av den andra knapparna anv. tryckte på
            switch (aCmd) {
                case "Avsluta":              Main.exitProgram();      break;
                case "Ny Samling":           dbc.createCollection();  break;
                case "Nytt kort":            dbc.newCard();           break;
                case "Skapa användare":      dbc.createUser();        break;
                case "Byt användare":        dbc.switchUser();        break;
                case "Ta bort användare":    dbc.removeUser();        break;
                case "Administrera databas": dbc.openTable();         break;
                case "Återställ databas":    dbc.resetDB();           break;
                case "Om":                   showAbout();
            }
        }
    }
}