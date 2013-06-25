
/**
 * Guest.java
 */

package user;
import cards.CardCollection;
import helpclasses.CollectionManager;
import interfaces.User;
import java.sql.*;
import java.util.*;

/**
 * Klassen beskriver en Gäst användare.
 * En gästanvändare har tillgång att titta i samtliga kortsamlingar, men
 * kan inte göra ändringar i databasen.
 * @author Kristian Mörling
 */

public class Guest implements User {
    private int userId = 1000;
    private String username = "guest";
    private ArrayList<CardCollection> cardCollections; //samtliga kortsamlingar
    
    public Guest() throws SQLException {
        boolean isUserGuest = true;
        cardCollections = CollectionManager.getTablesAsCollections(userId,
                isUserGuest);
    }

    /**
     * Returnerar användarens namn.
     * @return användarens namn
     */
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * Returnerar användarens id-nummer.
     * @return användarens id-nummer
     */
    @Override
    public int getUserId() {
        return userId;
    }
    
    /**
     * Returnerar ett värde som talar om ifall användaren har några tabeller.
     * @return <code>boolean</code> som talar om ifall användaren har några 
     * tabeller
     */
    @Override
    public boolean hasTables() {
        return !cardCollections.isEmpty();
    }
    
    /**
     * Returnerar en stränglista över namnen på samtliga kortsamlingar i databasen.
     * @return namnlista på samtliga kortsamlingar i databasen.
     */
    @Override
    public ArrayList<String> getUserCollections() {
        ArrayList<String> userTables = new ArrayList<String>();
        for (CardCollection cc : cardCollections) { //spara namn på tabeller 
            userTables.add(cc.getCollectionName()); //i lista
        }
        return userTables; //returnera lista med namn på kortsamlingar
    }
    
    /**
     * Sökfunktion, letar igenom listan över användarens kortsamlingar.
     * Sökfunktionen tar emot namnet på kortsamling att leta efter. Om kortsamling
     * hittas så skapar metoden ett <code>CardCollection</code>-objekt utav 
     * samlingen och returnerar objektet. I annat fall så returneras 
     * <code>null</code>.
     * @param collectionName namn på samling att leta efter
     * @return vid lyckad sökning returneras kortsamling i form av ett 
     * <code>CardCollection</code>-objekt. <br>
     * vid misslyckad sökning så returneras <code>null</code>
     */
    @Override
    public CardCollection getCardCollection(String collectionName) {
        String name = "";
        for (CardCollection cc: cardCollections) { //iterera igenom kortsamlingar
            name = cc.getCollectionName(); //spara namn på varje kortsamling och
            if (name.equals(collectionName)) { //jämför
                return cc; //kortsamling hittad
            }
        }
        return null; //kortsamling ej hittat
    }
}