
/**
 * User.java
 */

package interfaces;
import cards.CardCollection;
import java.util.*;

/**
 * Gränssnitt för användarklasser.
 * @author Kristian Mörling
 */
public interface User {
    
    /**
     * Metoden returnerar användarens id-nummer.
     * @return användarens id-nummer
     */
    int getUserId();
    
    /**
     * Metoden kontrollerar ifall användaren har några kortsamlingar/tabell.
     * @return <br>
     * true - användaren har kortsamlingar <br>
     * false - användaren saknar kortsamlingar
     */
    boolean hasTables();
    
    /**
     * Metoden returnerar användarens namn.
     * @return användarnamn
     */
    String getUsername();
    
    /**
     * Metoden returnerar en lista med namn över användarens kortsamlingar/tabeller.
     * @return lista över namnen på användarens kortsamlingar/taballer
     */
    public ArrayList<String> getUserCollections();
    
    /**
     * Sökfunktion som letar efter angiven kortsamling/tabell.
     * Hittas kortsamlingen så returnerar den i form av ett 
     * <code>CardCollection</code>-objekt. I annat fall så returneras 
     * <code>null</code>.
     * @param collectionName kortsamling/tabell att leta efter
     * @return kortsamling som hittats eller <code>null</code> ifall kortsamling 
     * inte hittats
     */
    CardCollection getCardCollection(String collectionName);
}