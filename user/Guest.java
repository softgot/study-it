
/**
 * Guest.java
 */

package user;
import helpclasses.GlobalVariables;
import java.sql.*;

/**
 * Klassen beskriver en Gäst användare.
 * En gästanvändare har tillgång att titta i samtliga kortsamlingar, men
 * kan inte göra ändringar i databasen.
 * @author Kristian Mörling
 */

public class Guest extends User {
    
    /**
     * Konstruktören hämtar användarens kortsamlingar och sparar dem i användarobjektet.
     * Sätter username och guestId till det som guest initierats till i 
     * GlobalVariables.
     * @throws java.sql.SQLException
     */
    public Guest() throws SQLException {
        super(GlobalVariables.guestId, GlobalVariables.guestUsername);
    }
}