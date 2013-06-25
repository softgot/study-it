
/**
 * SortSQL.java
 */

package helpclasses;
import database.SQL;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klassen sorterar datbasen genom att ge nya användare och nya kort rätt 
 * id-nummer.
 * @author Kristian Mörling
 */
public class SortSQL {
    
    /**
     * Metoden kontrollerar nästa id nummer för ett kort i en kortsamling/tabell.
     * <br><br>Metodens funktionalitet: Om en tabell innehåller 3 rader så antar 
     * programmet att nästa kortid är 4. Innan antagandet blir det slutgiltiga 
     * beslutet så kontrollerar programmet ifall kortid-numrena på korten i 
     * tabellen är 1, 2 och 3. <br><br>Skulle kortidnummer 2 saknas så tilldelas 
     * det nya kortet id-nummer 2. I annat fall, om det är så att korten i tabellen 
     * har 
     * id-numrena 1, 2 och 3 så ändras inte programmets första antagande om att 
     * nästa kortid är 4.
     * @param tableName namn på tabell kort ska placeras i
     * @return id nummer på nytt kort
     * @throws SQLException 
     */
    public static int nextCardId(String tableName) throws SQLException {
        //hämta kort i tabell sorterade efter kort-id
        int cardId;
        String query = "SELECT * FROM " + tableName + " ORDER BY CardId ASC";
        ResultSet rs = SQL.resultSet(query);
        
        cardId = 1;
        while (rs.next() && rs.getInt(1) == cardId) {
            cardId++;
        }
        rs.close();
        
        return cardId;
    }
    
    /**
     * Metoden bestämmer nästa användarid i tabellen.
     * Metoden fungerar på samma sätt som <code>nextCardId</code> med 
     * skillnaden att användarid-numrena börjar på 1000.
     * @return id-nummer för ny användare
     * @throws SQLException 
     */
    public static int nextUserId() throws SQLException {
        //hämta användare i tabell sorterade efter användar-id
        int userId;
        String query = "SELECT userId FROM users ORDER BY userid ASC";
        ResultSet rs = SQL.statement.executeQuery(query);
        
        userId = 1000;
        while (rs.next() && rs.getInt(1) == userId) {
            userId++;
        }
        rs.close();
        
        return userId;
    }
}