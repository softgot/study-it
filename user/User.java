
/**
 * User.java
 */

package user;

import cards.CardCollection;
import helpclasses.CollectionManager;
import helpclasses.GlobalVariables;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Klassen beskriver en användare.
 * De olika användarna är gästanvändare och vanliga användare. En gästanvändare
 * kan inte göra ändringar i databasen.
 * @author Kristian Mörling
 */
public class User {

    private int userId;
    private String username;
    protected ArrayList<CardCollection> cardCollections; //samtliga kortsamlingar

    public User(int userId, String username) throws SQLException {
        this.userId = userId;
        this.username = username;
        boolean isUserGuest = userId == GlobalVariables.guestId;
        cardCollections = CollectionManager.getTablesAsCollections(userId,
                isUserGuest);
    }

    /**
     * Metoden returnerar användarens id-nummer.
     *
     * @return användarens id-nummer
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Metoden kontrollerar ifall användaren har några kortsamlingar/tabell.
     *
     * @return <br>
     * true - användaren har kortsamlingar <br>
     * false - användaren saknar kortsamlingar
     */
    public boolean hasTables() {
        return !cardCollections.isEmpty();
    }

    /**
     * Metoden returnerar användarens namn.
     *
     * @return användarnamn
     */
    public String getUsername() {
        return username;
    }

    /**
     * Metoden returnerar en lista med namn över användarens
     * kortsamlingar/tabeller.
     *
     * @return lista över namnen på användarens kortsamlingar/taballer
     */
    public ArrayList<String> getUserCollections() {
        ArrayList<String> userTables = new ArrayList<>();
        cardCollections.forEach((cc) -> {
            userTables.add(cc.getCollectionName()); //lägg till samling i namnlista
        });
        return userTables; //return namnlista över kortsamlingar
    }

    /**
     * Sökfunktion som letar efter angiven kortsamling/tabell. Hittas
     * kortsamlingen så returnerar den i form av ett
     * <code>CardCollection</code>-objekt. I annat fall så returneras
     * <code>null</code>.
     *
     * @param collectionName kortsamling/tabell att leta efter
     * @return kortsamling som hittats eller <code>null</code> ifall kortsamling
     * inte hittats
     */
    public CardCollection getCardCollection(String collectionName) {
        for (CardCollection cc : cardCollections) { //sök igenom kortsamlingar
            String name = cc.getCollectionName();
            if (name.equals(collectionName)) {
                return cc; //hittade kortsamling, returnera kortsamling
            }
        }
        return null;
    }
}
