
/**
 * DBController.java
 */

package database;
import cards.*;
import controlcenter.ControlCenter;
import static controlcenter.ControlCenter.*;
import helpclasses.*;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;
import main.Main;
import user.*;

/**Klassen hanterar det externa tabellfönstret samt sköter överföring av data
 * mellan program och databas.
 * Händelser i tabellfönstret registreras i lyssnarklassen <code>DBInput</code>
 * som skickar vidare händelsen till denna klass som tar hand om anropet.
 *
 * @author Kristian Mörling
 */
public class DBController {
    private DBViewer dbViewer; //fristående fönster för att visa tabell
    private String currentTable = ""; //vald tabell i fönster
    private ControlCenter ctrlCenter;
    private final String rootPass = "mah"; //nyckel för att återställa program
    private String[] userTables; //lista över tabeller användaren kan se
    
    /**
     * Konstruktören initerar det fristående tabellfönstret samt initierar
     * lyssnarklass för tabellfönster.
     * Konstruktören tar även emot en referens till <code>ControlCenter</code>
     * som används för att meddela programmet när tabellfönstret stängts ner.
     * @param ctrlCenter referens till <code>ControlCenter</code>
     * @throws SQLException
     */
    public DBController(ControlCenter ctrlCenter) {
        this.ctrlCenter = ctrlCenter;
        DBInput dbInput = new DBInput(this);
        dbViewer = new DBViewer(dbInput);
    }
    
    /**
     * Metoden anropas när man väljer att öppna det fristående tabellfönster
     * (bilden på en kikare).
     * Innan fönstret visas, hämtar metoden namn på användarens tabeller
     * och sparar det i ett strängfält. Strängfältet skickas till grafikklassen
     * <code>DBViewer</code> där den placeras i en <code>JComboBox</code>
     * innehållande användarens tabellnamn. <br><br>Strängfältet används även för att 
     * visa den kortsamling man valt i tabellfönstrets <code>JComboBox</code> när
     * man tryckt på knappen "uppdatera". Har användaren valt index 0 i comboboxen 
     * så kan man ta reda på tabellens fullständiga namn genom att hämta det 
     * strängvärde som finns på index 0 i strängfältet. Det går inte att hämta 
     * tabellnamnet direkt från comboboxen därför att i comboboxen
     * så sparas inte tabellens/kortsamlingens fullständiga namn tabell_1001 
     * (med användarid). Utan i comboboxen så får tabellen namnet tabell.<br><br>
     * Precis innan det externa tabellfönstret visas, så aktiveras/inaktiveras
     * öppna knappen i tabellfönstret beroende på om användaren är vanlig 
     * användare eller gästanvändare.
     */
    public void openTable() {
        boolean isAnyCardOutdated, userIsGuest;
        double averageCardsLevel;
        CardCollection cardColl;
        if (user.hasTables()) { //öppna endast tabell ifall anv. har några samlingar

            userIsGuest = user instanceof Guest;
            userTables = new String[user.getUserCollections().size()];
            //spara lista över kortsamlingar som strängfält
            user.getUserCollections().toArray(userTables);
            
            //Eftersom strängfältet skickas till en JComboBox så blir det
            //första värdet i fältet automatiskt valt. Därför blir den
            //aktuella kortsamlingen det första värdet i fältet.
            currentTable = userTables[0];
            
            try {
                //uppdatera tabell varje gång tabellen öppnas
                cardColl = user.getCardCollection(currentTable); //hämta samling
                isAnyCardOutdated = cardColl.getOutdatedCount() > 0; //utgångna kort?
                Object[][] tableData = cardColl.getTable(); //tabelldata
                averageCardsLevel = cardColl.getAverageLevel(); //medelsnittsvärde på kunskapsnivå
                dbViewer.setTableData(tableData, isAnyCardOutdated, averageCardsLevel);
                dbViewer.toggleBtnOpen(userIsGuest); //inaktivera/aktivera öppna knapp
                dbViewer.openTable(userTables); //öppna tabellfönster
                
            } catch (NullPointerException ex) { //genereras av getCardCollection
                System.out.println(ex);         //när samling inte hittats
                showMessageDialog(null, "Kunde inte hämta kortsamling!");
            }
        } else { //användaren saknar samlingar
            showMessageDialog(null, "Användaren saknar kortsamling!"
                    + "\nVälj Fil -> Ny -> Ny samling för att skapa en samling");
        }
    }
    
    /**
     * Metoden visar den kortsamling man valt att visa i tabellfönstret.
     * Metoden anropas när man trycker på uppdatera knappen i tabellfönstret.
     * En vektor skapas av den kortsamling man valt att uppdatera innehållet av.
     * Vektorn används för att visa innehållet i kortsamlingen i tabellfönstret.
     */
    public void updateTable() {
        CardCollection cardColl;
        double averageCardsLevel;
        int nbrOfOutdatedCards;
        
        try {
            //hämta namn på vald kortsamling,
            //kontrollera ifall tabell innehåller några utgångna kort,
            //hämta även kortsamlingens genomsnittliga kunskapsnivå
            currentTable = userTables[dbViewer.getSelectedCmbBoxIndex()];
            cardColl = user.getCardCollection(currentTable);
            nbrOfOutdatedCards = cardColl.getOutdatedCount();
            averageCardsLevel = cardColl.getAverageLevel();
            
            //skapa och skicka tabelldata, tillsammand med argument som talar om
            //ifall det finns några utgångna kort
            Object[][] tableData = user.getCardCollection(currentTable).getTable();
            dbViewer.setTableData(tableData, nbrOfOutdatedCards > 0, averageCardsLevel);
        }
        catch (NullPointerException ex) { //genereras av getCardCollection
            System.out.println(ex);         //när kortsamling inte hittats
            showMessageDialog(null, "Kunde inte hämta kortsamling!");
        }
    }
    
    /**
     * Metoden förbereder kort för att öppnas i ett dialogfönster.
     * Först kontrolleras ifall användaren valt något kort i det fristående
     * tabellfönstret. Om användaren valt ett kort i tabellen så öppnas kortet.
     * Trycker användaren på ok efter att ha öppnat dialogfönstret så uppdateras
     * kortet till databasen, och användarens kortsamlingar hämtas på nytt
     * från databasen.
     * @throws SQLException
     */
    public void openCard() {
        Card card;
        CardViewer cv;
        
        //kontrollera ifall något kort är valt i tabellfönster
        if (dbViewer.isAnyRowSelected()) {
            try {
                //genom kortid-nummer hämta valt kort från kortsamling
                int selectedCard = dbViewer.getSelectedCard();
                card = user.getCardCollection(currentTable).getCard(selectedCard);
                
                //skapa objekt för att visa kort, öppna objekt i dialogfönster
                cv = new CardViewer(card);
                if (dbViewer.showCardDialog(cv) == OK_OPTION) {  
                    //uppdatera kort ifall användaren tryckt på ok, uppdatera tabell
                    saveCard(new Card( card.getCardId(), card.getExpiresAt(),
                            cv.getTextFront(), cv.getTextBack(), card.getLevel()) );
                    updateTable();
                }
            } catch (NullPointerException ex) { //genereras av CardCollection.getCard()
                System.out.println(ex);         //när kort inte finns
                showMessageDialog(null, "Kunde inte öppna kort");
            }
        } else { //inget kort valt, visa meddellande
            showNoRowSelectedMessage();
        }
    }
    
    /**
     * Metoden används för att ta bort ett kort från databasen.
     * @throws SQLException
     */
    public void removeCard() {
        int selectedCard;
        if (dbViewer.isAnyRowSelected()) { //kontrollera ifall något kort valts
            selectedCard = dbViewer.getSelectedCard(); //hämta valt kort
            try {
                CardManager.removeCard(currentTable, selectedCard); //ta bort från db
                updateUserTables(); //spara förändringar i användarobjekt
                updateTable(); //uppdatera fristående tabellfönster
            } catch (SQLException ex) {
                System.out.println(ex);
                showMessageDialog(null, "Kunde inte ta bort kort från databas!");
            }
        } else {
            showNoRowSelectedMessage(); //inget kort valt visa meddelande
        }
    }
    
    /**
     * Metoden öppnar ett dialogfönster som används för att skapa ett nytt kort.
     * @throws SQLException
     */
    public void newCard() {
        Card card;
        CardViewer cv;
        int cardId, nbrOfCards; //antal kort i aktuell samling
        
        nbrOfCards = user.getCardCollection(currCollection).getNbrOfCards();
        
        try {
            //spara kort ifall användaren tryckt på "ok"
            while ( (nbrOfCards <= 49) && 
            (dbViewer.showCardDialog( cv = new CardViewer() ) == OK_OPTION) ) {
                
                //bestäm det nya kortets idnummer
                cardId = SortSQL.nextCardId(currCollection);
                
                //skapa kort
                card = new Card( cardId, cv.getTextFront(), cv.getTextBack() );
                                
                //spara kort i databas
                CardManager.newCard(card, currCollection);
                
                //lägg till nytt kort i användarobjektets kortsamling
                updateUserTables();
                
                checkIfCardWasAdded(nbrOfCards); //kontrollera så att kort lades till i databas och användarobjekt
                nbrOfCards = user.getCardCollection(currCollection).getNbrOfCards();
            }
            
            if (nbrOfCards == 50) {
                showMessageDialog(null, "Maxgräns för antal kort nådd!"
                        + "\n\nKan inte lägga till fler än 50 kort.");
            }
            //kontrollera om samtliga kort har lagts till
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Kunde inte skapa kort!");
        }
    }
    
    public void checkIfCardWasAdded(int collSizeBeforeAdded) {
        int collSizeAfterAdded = user.getCardCollection(currCollection).getNbrOfCards();
        if ((collSizeBeforeAdded + 1) != collSizeAfterAdded) {
            showMessageDialog(Main.frame, "Kort lades inte till!", "Okänt fel", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metoden skickar ändringar på ett kort till databasen.
     * @param card kort att uppdatera
     * @throws SQLException
     */
    private void saveCard(Card card) {
        try {
            CardManager.updateCard(card, currentTable); //skicka kort till databas
            //hämta ny data från databas
            updateUserTables();
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Kunde inte uppdatera kort");
        }
    }
    
    /**
     * Metoden ställer in ny kunskapsnivå och utgångsdatum på ett kort.
     * Metoden anropas när man tryckt på "Ändra kunskapsnivå" i det popupfönster
     * som visar sig när man högerklickar i det fristående tabellfönstret.<br>
     * Metoden uppdaterar kunskapsnivå och utgångsdatum i databas samt i
     * användarobjekt. Därefter uppdateras det fristående tabellfönstret.
     */
    public void setCardLevel() {
        
        try {
            if (dbViewer.isAnyRowSelected()) {
                //hämta kortid till valt kort, hämta kort från samling
                int selectedCard = dbViewer.getSelectedCard(), level = 1;
                Card card = user.getCardCollection(currentTable).getCard(selectedCard);
                
                String input = "";
                while ( (input = showInputDialog("Mata in nivå på kort (1-8)"))
                        != null) {
                    level = Integer.parseInt(input);
                    if (level >= 1 && level <= 8) { //intervall 1-8
                        card.setLevel(level);
                        ExpireManager.setLevel(card, level); //ny nivå och utg.tid
                        saveCard(card); //till databas
                        updateTable(); //uppdatera tabellfönster
                        break;
                    }
                }
            } else {
                showNoRowSelectedMessage(); //inget kort valt, visa meddelande
            }
        } catch (NumberFormatException ex1) {
            System.out.println(ex1);
            showMessageDialog(null, "Fel vid inläsning av kunskapsnivå!");
        } catch (NullPointerException ex2) {
            System.out.println(ex2);
            showMessageDialog(null, "Kunde inte öppna kort");
        }
    }
    
    /**
     * Metoden återaktiverar knapparna som inaktiverades när det fristående
     * tabellfönstret öppnades.
     * Metoden anropas när man stänger ned det interna tabellfönstret.
     */
    protected void returnToCtrlCenter() {
        MenuItemGroup.enableGroup(MenuItemGroup.CTRL_CENTER);
        ctrlCenter.updateCtrlCenter(); //uppdatera programmets interna tabell
    }
    
    /**
     * Metoden hämtar tabeller från databas och sparar dem i användarobjekt.
     * Metoden används för att spara förändringar i databas till användarobjekt.
     * @throws SQLException
     */
    private void updateUserTables() {
        try {
            //hämta ny tabelldata från databas
            ((RegularUser)user).updateCollections();
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Kunde inte hämta användarens "
                    + "\ntabeller från databas!");
        }
    }
    
    /**
     * Metoden skapar en ny kortsamling.
     * @throws SQLException
     */
    public void createCollection()  {
        final int MAX_CHAR = 15;
        int userId;
        //öppna dialogfönster för att mata in namn
        String collName = showInputDialog("Namn på kortsamling (Max 15 tecken).");
        
        try {
            if (collName != null) { //spara tabell om användaren tryckt "ok"
                
                collName = collName.replace(" ", ""); //ta bort blanktecken
                
                int maxLength = (collName.length() < MAX_CHAR) ? collName.length():MAX_CHAR;
                collName = collName.substring(0, maxLength); //högst 15 tecken per tabellnamn
                userId = user.getUserId();
                
                //spara tabel, om tabellnamn inte redan existerar
                if ( !CollectionManager.tableExists(collName + "_" + userId)) {
                    CollectionManager.newCollection(userId, collName); //spara i db
                    updateUserTables(); //till användarobjekt
                    ctrlCenter.updateCtrlCenter(); //uppd. inre tabellfönster
                } else { //kortsamling finns redan
                    showMessageDialog(null, "Kortsamlingen " + collName +
                            " finns redan i databasen!");
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Kunde inte spara kortsamling i databas!"
                    + "\n\nKontrollera så att namnet på kortsamlingen"
                    + "\nendast innehåller bokstäver och siffror."
                    + "\n\nTecken som exempelvis   \"/\"   \"\\\"   \".\" "
                    + "\när ej tillåtna.");
        }
    }
    
    /**
     * Metoden raderar en kortsamling.
     * Först raderas tabellen från databasen sedan från användarobjektet.
     * @param tableName namn på tabell att radera
     * @throws SQLException
     */
    public void removeCollection(String tableName) {
        try {
            //visa endast tabellnamn utan användarid
            String displayName = tableName.substring(0, tableName.length() - 5);
            if ( (showConfirmDialog(null,
                    "Är du säker på att du vill ta bort " + displayName + "?",
                    "Ta bort tabell", YES_NO_OPTION)) == YES_OPTION) {
                CollectionManager.removeCollection(tableName);
                updateUserTables();
                ctrlCenter.updateCtrlCenter();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Ett fel inträffade vid borttagning av "
                    + "kortsamling!");
        }
    }
    
    /**
     * Autosparfunktion, överför kortsamlingar i användarobjekt till databas.
     * Anropas varje gång användaren återvänt till huvudfönstret efter att
     * ha öppnat en kortsamling genom öppna knappen i det inre tabellfönstret.
     * På detta sätt sparas de kort man lagt till. Även de kort man betygsatt
     * under en kortövning sparas trots att man avbrutit kortövningen.
     * @throws SQLException
     */
    public void collectionToDB() {
        try {
            if ( user instanceof RegularUser ) { //guest kan studera kort men inte ändra dem
                String collName = currCollection;
                CardCollection cCol = user.getCardCollection(collName);
                CollectionManager.updateCollection(cCol);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Misslyckades med att uppdatera "
                    + "\nkortsamlingar databas!");
        }
    }
    
    /**
     * Metoden tar bort en användare från databasen.
     * Efter att användaren tagits bort, så raderas även samtliga kortsamlingar
     * som tillhörde användaren.
     * @throws SQLException
     */
    public void removeUser() {
        try {
            int userId = user.getUserId();
            
            String message = "Radera " + user.getUsername() + " från databasen?";
            //ifall användaren är säker på sitt val, ta bort användaren
            if ( (showConfirmDialog(null, message, "", YES_NO_OPTION) == YES_OPTION ) ) {
                UserManager.removeUser(userId); //ta bort anv. fr. databas
                updateUser(new Guest()); //logga in som gäst
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Misslyckades med att ta bort "
                    + "användare från databas!");
        }
    }
    
    /**
     * Metoden byter användare.
     * @throws SQLException
     */
    public void switchUser() {
        try {
            updateUser(new Login().login()); //anropar dialogfönster för att logga in
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Kunde inte logga in användare!");
        }
    }
    
    /**
     * Metoden skapar en användare.
     * @throws SQLException
     */
    public void createUser() {
        try {
            updateUser(Login.newUser()); //öppna dialogfönster
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Misslyckades med att skapa "
                    + "ny användare i databas!");
        }
    }
    
    /**
     * Metoden uppdaterar programmets användarobjekt till nyinloggad användare.
     * Anropas när man bytt användare eller tagit bort en användare. Då man blir
     * automatiskt inloggad som gäst, vid borttagning av användare.
     * @param user nyinloggad användare
     * @throws SQLException
     */
    private void updateUser(User user) {
        if ( user != null ) { //lyckad inloggning!
            ControlCenter.user = user;
            //aktivera knappar efter användare
            MenuItemGroup.setUser(user);
            MenuItemGroup.enableGroup(MenuItemGroup.CTRL_CENTER);
            ctrlCenter.updateCtrlCenter();
            Main.setLoggedInUserLabel(user.getUsername());
        }
    }
    
    /**
     * Öppnar popupfönster när användaren högerklickat inuti det fristående
     * tabellfönstret. Endast vanlig användare kan öppna popupfönster.
     * @param mouseXPos muspekarens placering i x-led
     * @param mouseYPos muspekarens placering i y-led
     */
    protected void showPopupMenu(int mouseXPos, int mouseYPos) {
        if (user instanceof RegularUser) {
            dbViewer.showPopupMenu(mouseXPos, mouseYPos);
        }
    }
    
    /**
     * Visar ett meddelande som anropas när användaren valt öppna eller ta bort
     * ett kort när inget kort har valts.
     */
    private void showNoRowSelectedMessage() {
        showMessageDialog(null, "Du har inte valt något kort!");
    }
    
    /**
     * Återställer databasen genom att ta bort allt innehåll och lägga till
     * nytt innehåll genom att exekvera ett SQL-skript.
     * @throws SQLException
     */
    public void resetDB() {
        String warningMsg = "Att återställa databasen innebär att programmet "
                + "\nåtergår till sina standardinställningar. \n\nSamtliga "
                + "tabeller och användare raderas från databasen. "
                + "\nÄr du säker på ditt val?\n\n",
                message = "Ange administratörens lösenord:\n", input = "";
        int choice = showConfirmDialog(null, warningMsg, //visa varningsmeddellande
                "Återställ databas", YES_NO_OPTION, WARNING_MESSAGE);
        
        try {
            //ifall användaren bekräftat sitt och val och skrivit in rätt administratör-
            //lösenord, återställ databas
            if ( (choice == YES_OPTION) && (input=showInputDialog(Main.frame, message)) != null ) {
                if ( input.equals(rootPass) ) {
                    Main.setWaitCursor(true);
                    
                    UserManager.resetDB();
                    Main.setWaitCursor(false);
                    showMessageDialog(null, "Databas återställd");
                    updateUser(new Guest()); //logga in som gästanvändare
                } else {
                    showMessageDialog(null, "Felaktigt lösenord!");
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            showMessageDialog(null, "Ett fel uppstod vid försök "
                    + "\ntill att återställa databasen!");
        }
    }
        /**
     * Metoden redigerar det kort som visas i studiefönstret.
     * Metoden anropas från menuvalet "Redigera kort". Anropet sker enligt
     * följande ordning: Menubar -> SSInput -> StudySession -> DBController
     * @param currentCard det aktuella kortet i studiefönstret
     */
    public void editCurrentSessionCard(Card currCard) {
        CardViewer cv = new CardViewer(currCard);
        
        if (dbViewer.showCardDialog(cv) == OK_OPTION) {
            currCard.setFrontSide(cv.getTextFront());
            currCard.setBackSide(cv.getTextBack());
        }
    }
}