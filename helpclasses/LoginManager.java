
/**
 * LoginManager.java
 */

package helpclasses;
import database.SQL;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klassen används vid inloggning för att hämta tillgängliga användare samt för 
 * att kontrollera att lösenordet stämmer.
 * @author Kristian Mörling
 */
public class LoginManager {
    
    /**
     * Metoden kontrollerar ifall angivet lösenord stämmer.
     * @param userId användare att logga in
     * @param password lösenord att kontrollera ifall korrekt
     * @return <code>boolean true</code> = korrekt inloggning <br>
     * <code>boolean false</code> = inkorrekt inloggning
     * @throws SQLException 
     */
    public static boolean isLoginCorrect(int userId, String password)
            throws SQLException {
        boolean loginIsCorrect = false;
        //kontrollera ifall inmatat lösenord stämmer för vald användare
        String command = 
                "SELECT userid, "
                + "username, "
                + "password "
                + "FROM users " +
                "WHERE userid='" + userId + 
                "' AND password = '" + password + "'";
        
        ResultSet rs = SQL.resultSet(command);
        if (rs.next()) { //korrekt inloggning
            loginIsCorrect = true;
        }
        rs.close();
        return loginIsCorrect; //felaktig inloggning
    }
    
    /**
     * Metoden returnerar en vektor över tillgängliga användare.
     * Vektorn innehåller användarens namn och användarid. <br><br>Vektorn 
     * används i den rullista (<code>JComboBox</code>) som används för att välja 
     * användare vid inloggning.
     * @return vektor över tillgängliga användarnamn och användarid
     * @throws SQLException 
     */
    public static String[][] getUsers() throws SQLException {
        int nbrOfRows = 0;
        String[][] usersNameAndId;
        String command = "SELECT userId, userName FROM users";
        ResultSet rs = SQL.resultSet(command);
        
        while (rs.next()) {
            nbrOfRows++;
        }
        rs.close();
        
        usersNameAndId = new String[ nbrOfRows ][ 2 ]; 
        
        rs = SQL.resultSet(command);
        for (int i = 0; rs.next(); i++) { 
            usersNameAndId[i][0] = rs.getString(1);
            usersNameAndId[i][1] = rs.getString(2);
        }
        rs.close();
        return usersNameAndId;
    }
}