
/**
 * CardManager.java
 */

package helpclasses;
import cards.Card;
import database.SQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klassen sköter anrop till databasen som ändrar, hämtar, tar bort eller lägg
 * till kort.
 * @author Kristian Mörling
 */
public class CardManager {
    
    /**
     * Metoden används för att lägga till ett nytt kort.
     * @param card kort att lägga till
     * @param collectionName kortsamling/tabell att lägga till kort i
     * @throws SQLException 
     */
    public static void newCard(Card card, String collectionName) throws SQLException {
        String command = "INSERT INTO " + collectionName + " VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = SQL.connection.prepareStatement(command);
        
        //förbered kolumndata för överföring och spara i db
        ps.setInt(1, card.getCardId()); 
        ps.setLong(2, card.getExpiresAt()); 
        ps.setString(3, card.getFrontSide());
        ps.setString(4, card.getBackSide());
        ps.setInt(5, card.getLevel()); 
        ps.executeUpdate(); //spara kort på en rad 
        ps.close();
    }
    
    /**
     * Metoden uppdaterar ett kort med ny data.
     * @param card kort att uppdatera
     * @param collectionName kortsamling/tabell som kort tillhör
     * @throws SQLException 
     */
    public static void updateCard(Card card, String collectionName) throws SQLException {
        //förbered kommando, som hittar kort efter kort-id och sparar nya uppgifter
        String statement = "UPDATE " + collectionName + " SET ExpiresAt = ?, " +
                "FrontSide = ?, BackSide = ?, Level = ? WHERE CardId = ?";
        //uppdatera samtliga data förutom kortets id-nummer
        PreparedStatement ps = SQL.connection.prepareStatement(statement);
        ps.setLong(1, card.getExpiresAt());
        ps.setString(2, card.getFrontSide());
        ps.setString(3, card.getBackSide());
        ps.setInt(4, card.getLevel());
        ps.setInt(5, card.getCardId());
        ps.executeUpdate(); //uppdatera kort
        ps.close();
    }
    
    /**
     * Metoden används för att ställa in samtliga kort i en kortsamling som 
     * utgångna.
     * Det nya utgångstiden för korten blir nuvarande tid minus 10 sekunder.
     * @param tableName kortsamlingen i vilken korten ska ställas in som utgångna
     * @throws SQLException 
     */
    public static void resetEveryCard(String tableName) throws SQLException {
        long currTime = System.currentTimeMillis(); //nuvarande tid i ms
        ResultSet rs = null;
        PreparedStatement ps;
        
        String query = "SELECT * FROM " + tableName; //hämta samtliga kort
        rs = SQL.resultSet(query);
        
        String statement = 
                "UPDATE " + tableName +
                " SET ExpiresAt = ?" +
                " WHERE CardId= ?";
        
        //itererar genom kortsamling och ställer in ny utgångstid på varje kort
        while (rs.next()) {
            ps = SQL.connection.prepareStatement(statement);
            ps.setLong(1, currTime);
            ps.setInt(2, rs.getInt(1));
            ps.executeUpdate(); 
            ps.close();
        }
        rs.close();
    }
    
    /**
     * Metoden används vid borttagning av kort.
     * @param tableName kortsamling/tabell i vilken ett kort ska raderas
     * @param cardId kortid-nummer på kort att radera
     * @throws SQLException 
     */
    public static void removeCard(String tableName, int cardId) throws SQLException {
        //förbered kommando som letar upp kort efter kortid-nummer
        String command = "DELETE FROM " + tableName + " WHERE cardId = ?";
        
        PreparedStatement ps = SQL.connection.prepareStatement(command);
        ps.setInt(1, cardId);
        ps.executeUpdate();
        ps.close();
    }
}