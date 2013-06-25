
/**
 * UserManager.java
 */

package helpclasses;
import database.SQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klassen sköter anrop till databasen som tar bort eller lägger till användare.
 * Klassen sköter även återställning av databas genom att exekvera SQL-skriptet
 * "projekt.sql".
 * @author Kristian Mörling
 */
public class UserManager {
    
    /**
     * Skickar ny användare till databas.
     * Metoden returnerar användarens id nummer som används för att skapa 
     * användarobjektet.
     * @param username namn på användare
     * @param password lösenord till användare
     * @return användarens id-nummer
     * @throws SQLException 
     */
    public static int createUser(String username, String password) throws SQLException {
        String statement = "INSERT INTO users VALUES (?, ?, ?)";
        int userId = SortSQL.nextUserId(); //bestäm användarens id nummer
        PreparedStatement ps;
        
        //spara användarid, användarnamn och användarlösenord i db
        ps = SQL.connection.prepareStatement(statement);
        ps.setInt(1, userId);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.execute();
        ps.close();
        return userId;
    }
    
    /**
     * Metoden tar bort en användare från databasen.
     * @param userId id nummer på användare att ta bort
     * @throws SQLException 
     */
    public static void removeUser(int userId) throws SQLException {
        String command = "DELETE FROM users WHERE UserId = ?";
        PreparedStatement ps = SQL.connection.prepareStatement(command);
        ps.setInt(1, userId);
        ps.executeUpdate(); //ta bort användare
        ps.close();
        
        //radera även användarens tabeller
        CollectionManager.removeTables(userId);
    }
    
    /**
     * Metoden tömmer databasen för att sedan återställa databasen genom att 
     * exekvera skriptet "projekt.sql".
     * @throws SQLException 
     */
    public static void resetDB() throws SQLException {
        //hämta info om samtliga tabeller
        String query = "SELECT name from sqlite_master "
                    + "where type in ('table','view') "
                    + "AND name like '%1___'";
        
        ResultSet rs = SQL.resultSet(query);
        
        //läs in tabeller som tilhör program
        List <String> tables = new ArrayList<String>();
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        
        //ta bort samtliga tabeller
        PreparedStatement ps;
        String statement = "";
        while (!tables.isEmpty()) {
            statement = "DROP TABLE " + tables.remove(0);
            ps = SQL.connection.prepareStatement(statement);
            ps.execute();
            ps.close();
        }
        
        //ta bort samtliga användare förutom guest
        statement = "DELETE FROM users WHERE userid != '1000'";
        ps = SQL.connection.prepareStatement(statement);
        ps.execute();
        ps.close();
    }
}
