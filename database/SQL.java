
/**
 * SQL.java
 */

package database;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Klassen används för att koppla upp programmet mot databasen, koppla ned 
 * förbindelsen samt för att hämta information från databasen.
 * @author Kristian Mörling
 */
public class SQL {
    public static Connection connection = null;
    public static Statement statement;
    public static ResultSet rs;
    public static ResultSetMetaData rsmd;
    public static PreparedStatement ps;
    
    /**
     * Metoden kopplar upp programmet mot databasen.
     * @throws SQLException 
     */
    public static void connect(String dbPath) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex); //uppkopplingen misslyckades avsluta program
            JOptionPane.showMessageDialog(null, "Kunde inte initiera "
                    + "databasdrivrutin!\n\nProgram avslutas...");
            System.exit(1);
        }
    }

    /**
     * Metoden kopplar ner förbindelsen.
     * @throws SQLException 
     */
    public static void disconnect() throws SQLException {
        connection.close();
        statement.close();
    }

    /**
     * Används för att hämta information från databasen.
     * Det kommando man vill utföra skickas som argument. Kommandot 
     * skickar tillbaka ett resultat i form av <code>ResultSet</code>.
     * Resulatet returneras av metoden.
     * @param command kommando att skicka till databas
     * @return resultat från kommando i form av <code>ResultSet</code>
     * @throws SQLException 
     */
    public static ResultSet resultSet(String command) throws SQLException {
        return statement.executeQuery(command);
    }
}