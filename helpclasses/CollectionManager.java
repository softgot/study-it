
/**
 * CollectionManager.java
 */

package helpclasses;
import cards.Card;
import cards.CardCollection;
import database.SQL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hjälpklassen sköter anrop till databasen som ändrar, hämtar, tar bort eller lägg
 * till kortsamlingar. 
 * @author Kristian Mörling
 */
public class CollectionManager {
    
    /** Metoden används för att skapa en lista över användarens kortsamlingar.
     * Varje tabell i databasen representerar en kortsamling. I programmet 
     * beskrivs en kortsamling genom klassen <code>CardCollection</code>.
     * Metoden skapar ett <code>CardCollection</code>-objekt av varje kortsamling
     * som tillhör användaren och sparar objekten i en lista som returneras.
     * @param userId id på användare att hämta tabeller åt
     * @param isUserGuest <code>true</code> = användare är gästanvändare<br>
     * <code>false</code> = användare är vanlig användare
     * @return lista innehållande <code>CardCollection</code>-objekt som 
     * representerar användarens kortsamlingar.<br>
     * Eller om det är så att användaren är <code>Guest</code> så returneras 
     * en lista på samtliga kortsamlingar.
     * @throws SQLException 
     */
    public static ArrayList<CardCollection>
            getTablesAsCollections(int userId, boolean isUserGuest) throws SQLException {
        ResultSet rs;
        String query = "SELECT name from sqlite_master "
                    + "where type in ('table','view') "
                    + "AND name like '%" + userId + "'";
        
        if (isUserGuest) {
            //Gäst, hämtar samtliga tabeller som slutar med ett användarid
            //fungerar endast när databasen inte har fler än 1000 användare
            query = "SELECT name from sqlite_master "
                    + "where type in ('table','view') "
                    + "AND name like '%1___'"; 
        }
        
        //Skapa 2 listor, i den ena lista sparas namnen över tabeller att 
        //skapa CardCollection objekt utav. I den andra listan sparas 
        //CardCollection objekten.
        ArrayList<String> tableNames = new ArrayList<String>();
        ArrayList<CardCollection> cc = new ArrayList<CardCollection>();
        
        //spara namn över tabeller att skapa CardCollection objekt utav
        rs = SQL.resultSet(query);
        while(rs.next()) {
            tableNames.add(rs.getString(1));
        }
        rs.close();
        //spara tabeller som CardCollection objekt i lista
        for (Iterator it = tableNames.iterator(); it.hasNext(); ) {
            cc.add(getTableAsCollection( it.next().toString() ) );
        }
        return cc; //returnera lista innehållande CardCollection-objekt
    }
    
    /**
     * Metoden skapar ett <code>Card</code>-objekt av varje rad i en tabell och 
     * sparar sedan <code>Card</code>-objekten i en lista av typen 
     * <code>CardCollection</code>.
     * @param tableName namn på tabell att skapa objekt av 
     * @return <code>CardCollection</code>-objekt innehållande <code>Card</code>:s
     * @throws SQLException 
     */
    private static CardCollection getTableAsCollection(String tableName) throws SQLException {
        CardCollection cardCollection; //lista att spara varje kort i
        ArrayList<Card> cards = new ArrayList<Card>();
        ResultSet rs = SQL.resultSet( "SELECT * FROM " + tableName + " ORDER BY CardId ASC" );
        
        //spara varje rad från tabell som ett kort i en lista
        while (rs.next()) {
            cards.add(new Card(rs.getInt(1), rs.getLong(2), rs.getString(3),
                    rs.getString(4), rs.getInt(5)));
        }
        rs.close();
        //lista med kort blir ett objekt av typen CardCollection
        cardCollection = new CardCollection(cards, tableName);
        return cardCollection;
    }
    
    /**
     * Metoden tar bort en tabell/kortsamling från databasen.
     * @param collectionName namn på tabell att radera
     * @throws SQLException 
     */
    public static void removeCollection(String collectionName) throws SQLException {
        String stmt = "DROP TABLE " + collectionName;
        PreparedStatement ps = SQL.connection.prepareStatement(stmt);
        ps.execute();
        ps.close();
    }
    
    /**
     * Skapar en ny tabell/kortsamling i databasen.
     * @param userId användarid på användare som tabell ska tillhöra
     * @param collectionName namn på tabell/kortsamling
     * @throws SQLException 
     */
    public static void newCollection(int userId, String collectionName)
            throws SQLException {
        collectionName = collectionName.toLowerCase(); //tabellnamn skrivs med gemener
        String stmt = "CREATE TABLE " + collectionName + "_" + userId +
                "(CardId int, " +
                "ExpiresAt bigint, " +
                "FrontSide varchar(255), " +
                "BackSide varchar(255), " +
                "Level int" +
                ")";
        
        PreparedStatement ps = SQL.connection.prepareStatement(stmt);
        ps.execute(); //skapa ny tabell/kortsamling
        ps.close();
    }
    
    /**
     * Metoden uppdaterar korten i en kortsamling av typen 
     * <code>CardCollection</code>.
     * Metoden anropas efter en genomgång av en kortsamling för att uppdatera 
     * kunskapsnivåerna på korten användaren studerat.
     * @param cCol kortsamling att uppdatera
     * @throws SQLException 
     */
    public static void updateCollection(CardCollection cCol) throws SQLException {
        PreparedStatement ps = null;
        ArrayList<Card> cards = cCol.getCards(); //spara samtliga kort i en lista 
        String command = "", collName = cCol.getCollectionName();
        
        //uppdatera samtliga kolumner för varje kort förutom kortets id-nummer
        for (Card c : cards) {
            command = "UPDATE " + collName + 
                    " SET ExpiresAt = ?, " +
                    "FrontSide = ?, " + 
                    "BackSide = ?, " + 
                    "Level = ? " + 
                    "WHERE CardId= ?";
            ps = SQL.connection.prepareStatement(command);
            ps.setLong( 1, c.getExpiresAt() );
            ps.setString( 2, c.getFrontSide() );
            ps.setString( 3, c.getBackSide() );
            ps.setInt( 4, c.getLevel() );
            ps.setInt( 5, c.getCardId() );
            ps.executeUpdate();
            ps.close();
        }
    }
    
    /**
     * Metoden tar bort önskade tabeller.
     * Metoden anropas vid borttagning av användare för att radera samtliga 
     * tabeller som tillhör användaren.
     * @param userId användarid på användare vars tabeller ska tas bort 
     * @throws SQLException 
     */
    public static void removeTables(int userId) throws SQLException {        
        String query = 
                "SELECT name from sqlite_master "
                + "where type in ('table','view') "
                + "AND name like '%" + userId + "'";
        ResultSet rs = SQL.resultSet(query);
        
        //hämta användartabeller
        List<String> tables = new ArrayList<String>();
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        
        //ta bort tabeller
        String statement;
        PreparedStatement ps;
        while ( !tables.isEmpty() ) {
            statement = "DROP TABLE " + tables.remove(0);
            ps = SQL.connection.prepareStatement(statement);
            ps.execute();
            ps.close();
        }
    }
    
    /**
     * Kontrollerar om en tabell inte redan existerar i databasen.
     * @param tableName tabell att letar efter i databas
     * @return true - om tabell existerar, false - ifall tabell inte existerar
     * @throws SQLException 
     */
    public static boolean tableExists(String tableName) throws SQLException {
        //tabellnamn slutar på _1001, _1002 osv. där siffrorna är använderens id-nummer
        boolean tableExists = false;
        String command = "SELECT name from sqlite_master "
                + "where type in "
                + "('table','view') "
                + "AND name like '%" 
                + tableName + "'"; //tabell_1001
        ResultSet rs = SQL.resultSet(command);
        tableExists = rs.next();
        rs.close();
        return tableExists;
    }
}
