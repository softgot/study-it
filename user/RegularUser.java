
/**
 * RegularUser.java
 */

package user;
import helpclasses.CollectionManager;
import java.sql.*;

/**
 * Klassen beskriver en vanlig användare, en användare som inte är av typen 
 * <code>Guest</code>.
 * @author Kristian Mörling
 */
public class RegularUser extends User {
    
    /**
     * Konstruktören hämtar användarens kortsamlingar och sparar dem i användarobjektet.
     * @param userId användarens id-nummer
     * @param username användarens namn
     * @throws java.sql.SQLException
     */
    public RegularUser(int userId, String username) throws SQLException {
        super(userId, username);
    }

    /**
     * Uppdaterar användarens kortsamling genom att hämta ny data från databasen.
     * Kortsamlingar hämtas från databasen för att hålla användarobjektets
     * kortsamlingar uppdaterade.
     * @throws SQLException 
     */
    public void updateCollections() throws SQLException {
        boolean isUserGuest = false;
        //hämta användarens kortsamlingar
        super.cardCollections = CollectionManager.getTablesAsCollections(super.getUserId(), 
                isUserGuest);
    }
}