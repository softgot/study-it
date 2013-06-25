
/**
 * RegularUser.java
 */

package user;
import cards.CardCollection;
import helpclasses.CollectionManager;
import interfaces.User;
import java.sql.*;
import java.util.*;

/**
 * Klassen beskriver en vanlig användare, en användare som inte är av typen 
 * <code>Guest</code>.
 * @author Kristian Mörling
 */
public class RegularUser implements User {
    private int userId;
    private String username;
    private ArrayList<CardCollection> cardCollections; //användarens kortsamlingar
    
    /**
     * Konstruktören hämtar användarens kortsamlingar och sparar dem i användarobjektet.
     * Konstruktören har även 5 parametrar som används för att spara 
     * användarens id-nummer, namn, personnummer, adress och telefonnummer.
     * @param userId användarens id-nummer
     * @param username användarens namn
     */
    public RegularUser(int userId, String username) throws SQLException {
        this.username = username;
        this.userId = userId;
        //hämta användarens kortsamlingar
        boolean isUserGuest = false;
        cardCollections = CollectionManager.
                getTablesAsCollections(userId, isUserGuest);
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
     * Talar om ifall användaren har några tabeller.
     * @return true = användaren har tabeller, false = användaren saknar tabeller
     */
    @Override
    public boolean hasTables() {
        return !cardCollections.isEmpty();
    }

    /**
     * Returnerar en lista över namnen på användarens kortsamlingar.
     * @return stränglista innehållande namnen på användarens kortsamlingar
     */
    @Override
    public ArrayList<String> getUserCollections() {
        ArrayList<String> userTables = new ArrayList<String>(); 
        for (CardCollection cc : cardCollections) {
            userTables.add(cc.getCollectionName()); //lägg till samling i namnlista
        }
        return userTables; //return namnlista över kortsamlingar
    }
    
    /**
     * Sökfunktion, letar igenom användarens kortsamlingar för att hitta 
     * önskad kortsamling.
     * @param collectionName namn på kortsamling att leta efter
     * @return ifall kortsamling hittats så returneras samlingen i form av 
     * ett <code>CardCollection</code>-objekt, i annat fall så returneras
     * <code>null</code>
     */
    @Override
    public CardCollection getCardCollection(String collectionName) {
        String name = "";
        for (CardCollection cc: cardCollections) { //sök igenom kortsamlingar
            name = cc.getCollectionName();
            if (name.equals(collectionName)) { 
                return cc; //hittade kortsamling, returnera kortsamling
            }
        }
        return null;
    }    //returnerar alla kortsamlingar(tabeller) som tillhör användaren

    /**
     * Uppdaterar användarens kortsamling genom att hämta ny data från databasen.
     * Kortsamlingar hämtas från databasen för att hålla användarobjektets
     * kortsamlingar uppdaterade.
     * @throws SQLException 
     */
    public void updateCollections() throws SQLException {
        boolean isUserGuest = false;
        //hämta användarens kortsamlingar
        cardCollections = CollectionManager.getTablesAsCollections(userId, 
                isUserGuest);
    }
}