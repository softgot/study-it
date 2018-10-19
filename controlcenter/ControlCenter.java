
/*
 * ControlCenter.java
 */

package controlcenter;
import database.DBController;
import helpclasses.MenuItemGroup;
import user.User;
import java.awt.*;
import javax.swing.*;
import main.ButtonBar;
import main.Main;
import menubar.Menubar;
import studysession.SSController;

/**
 * Klassen är mer eller mindre kärnan i programmet. De flesta anrop i programmet 
 * färdas först genom denna klass. Klassen fungerar som ett kommunikationscentrum.
 * Klassen gör det möjligt för objekten i programmet att kommunicera med varandra 
 * via klassen.
 * 
 * <br><br>Också initierar klassen de flesta objekten i prorammet.
 * Klassen har även en <code>CardLayout</code> med 
 * tre olika fönster: <br><br>
 * 1. Fönstret över användarens tabeller <br>2. Fönster för att 
 * göra val inför en kortövning <br>3. Fönster för att studera kort.
 * @author Kristian Mörling
 */
public class ControlCenter extends JPanel {
    private Table table; //tabell över kortsamlingar och utgångna kort
    public static User user; 
    private SSController ssCtrler; //objekt för studerandefönster
    private DBController dbCtrler; //databasobjekt
    public static boolean sessionActive; //håller reda på när kort studeras
    public static String currCollection = ""; //håller reda på aktuell samling
    public static CardLayout cardLayout = new CardLayout();
    
    /**
     * Konstruktören initierar de olika objekten i programmet.
     * Som argument till de olika objekten skickas en referens till
     * denna klassen. Konstruktören sparar även det användarobjekt och
     * objekt för den östra knappmenyn som skapas vid start av programmet.
     * @param user användarobjekt
     * @param buttonBar objekt för genvägsmeny 
     * @param menubar menybar
     */
    public ControlCenter(User user, ButtonBar buttonBar, Menubar menubar) {
        super(cardLayout);
        ControlCenter.user = user;
        
        //initiera progr. objekt, skicka med referens till denna klass
        buttonBar.setCtrlCenter(this);
        table = new Table(this);
        dbCtrler = new DBController(this);
        //ge menuvalsklassen referens till databasklassen
        menubar.setDbController(dbCtrler);
        ssCtrler = new SSController(this, menubar, dbCtrler); 
                
        table.setBorder(BorderFactory.createTitledBorder(null, "Kortsamlingar",
                0, 0, new Font("Serif", Font.BOLD, 22))); //rubrikram
        add(table, "MAIN_PANE"); //lägg till tabellfönster i layout
        cardLayout.show(this, "MAIN_PANE"); //visa tabellfönster vid start
    }
    
    /**
     * Metoden byter till fönstret som används för att öppna en kortsamling. 
     * Metoden anropas när man trycker på någon av öppna knapparna 
     * till höger om kortsamlingarnas namn i det interna tabellfönstret. 
     * Som argument till metoden skickas namnet på kortsamlingen man valt
     * att öppna.
     * @param collectionName namn på kortsamling att öppna 
     */
    public void showPreparePane(String collectionName) {
        MenuItemGroup.enableGroup(MenuItemGroup.SS_CONFIG); //aktivera knappar för fönster
        Main.setFrameTitle(collectionName.substring(0, collectionName.length() - 5));
        currCollection = collectionName; 
        ssCtrler.showCardCollection(); //visa fönster
    }
    
    /**
     * Metoden byter till huvudfönstret med den interna tabellen.
     * Metoden anropas när man återvänder från en kortövning eller när man
     * stängt ned fönstret för det externa tabellfönstret. Metoden anropar metoder
     * i databasklassen som uppdaterar användarens samlingar mot databasen och 
     * uppdaterarar den interna tabellen. 
     */
    public void showControlCenter() {
        Main.setWaitCursor(true);
        Main.setFrameTitle(""); //visa ingen ramtitel
        sessionActive = false; //stäng av timer ifall den är på
        dbCtrler.collectionToDB(); //spara kort i databas
        updateCtrlCenter(); //uppdatera intern tabell
        MenuItemGroup.enableGroup(MenuItemGroup.CTRL_CENTER); //akt. fönsterknappar
        cardLayout.show(this, "MAIN_PANE"); //visa intern tabell
        Main.setWaitCursor(false);
    }
    
    /**
     * Metoden ställer in den inloggade användaren.
     * Används vid byte av användare.
     * @param user nyinloggad användare
     */
    public void setUser(User user) {
        ControlCenter.user = user;
    }
    
    /**
     * Metoden returnerar användareobjektet för den inloggade användaren.
     * @return användarobjekt för inloggad användare
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Metoden returnerar objektet till databasklassen.
     * @return objekt för hantering av databas
     */
    public DBController getDBController() {
        return dbCtrler;
    }
    
    /**
     * Metoden returnerar objektet som hanterar fönster för studier av kort.
     * @return objekt som hanterar studiefönster
     */
    public SSController getSSController() {
        return ssCtrler;
    }

    /**
     * Uppdaterar den interna tabellen.
     */
    public void updateCtrlCenter() {
        table.setTable(); 
    }
}