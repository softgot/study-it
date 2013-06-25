
/**
 * Card.java
 */

package cards;

/**
 * Klassen beskriver ett kort med fram och baksida.
 * I databasen så representerar varje rad i en tabell ett kort. Attributen i 
 * denna klass blir kolumnerna på raden.
 * @author Kristian Mörling
 */
public class Card implements Comparable<Card> {
    private long expiresAt;
    private int level, cardId;
    private String frontSide, backSide;
      
    /**
     * Konstruktören skapar ett nytt kortobjekt med nivå 1 i kunskapsnivå och 
     * en utgångstid på 1 dag.
     * @param cardId kortets unika id nummer för tabellen
     * @param frontSide kortets framsidetext
     * @param backSide kortets baksidetext
     */
    public Card(int cardId, String frontSide, String backSide) {
        this(cardId, 86400000L+System.currentTimeMillis(), frontSide, backSide, 1);
    }

    /**
     * Konstruktören skapar ett nytt kortobjekt med önskade attribut.
     * @param cardId kortets unika id nummer för tabellen 
     * @param expiresAt kortets utgångsdatum
     * @param frontSide kortets framsidetext
     * @param backSide kortets baksidetext
     * @param level kunskapsnivån på kortet inom intervallet 1 till 8
     */
    public Card(int cardId, long expiresAt, String frontSide, String backSide, 
            int level) {
        this.cardId = cardId;
        this.expiresAt = expiresAt;
        this.frontSide = frontSide;
        this.backSide = backSide;
        this.level = level;
    }
    
    /**
     * Ställer in kortets utgångsdatum.
     * @param expiresAt kortets utgångsdatum angivet i millisekunder
     */
    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Bestämmer kortets framsidetext.
     * @param frontSide kortets framsidetext
     */
    public void setFrontSide(String frontSide) {
        this.frontSide = frontSide;
    }
    
    /**
     * Bestämmer kortets baksidetext.
     * @param backSide kortets baksidetext
     */
    public void setBackSide(String backSide) {
        this.backSide = backSide;
    }
    
    /**
     * Bestämmer kunskapsnivån på kortet.
     * @param level kunskapsnivån på kortet
     */
    public void setLevel(int level) {
        this.level = level;
    }
    
    /**
     * Returnerar kortets unika id-nummer i kortsamlingen.
     * @return kortets id-nummer
     */
    public int getCardId() {
        return cardId;
    }
    
    /**
     * Returnerar utångsdatum på kortet.
     * @return utgångsdatum angivet i millisekunder
     */
    public long getExpiresAt() {
        return expiresAt;
    }
    
    /**
     * Returnerar kortets framsidetext.
     * @return kortets framsidetext
     */
    public String getFrontSide() {
        return frontSide;
    }
    
    /**
     * Returnerar kortets baksidetext.
     * @return kortets baksidetext
     */
    public String getBackSide() {
        return backSide;
    }

    /**
     * Metoden returnerar kunskapsnivån användaren erhåller på kortet.
     * @return kunskapsnivån på kortet
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Byter ut innehållet i kortet mot innehållet i det kort som skickas som argument.
     * @param card Kort att ta innehåll ifrån
     */
    public void updateCard(Card card) {
        // Ersätt kort mot kort i parameter
        cardId = card.getCardId();
        expiresAt = card.getExpiresAt();
        frontSide = card.getFrontSide();
        backSide = card.getBackSide();
        level = card.getLevel();
    }

    /**
     * Metoden jämför kortobjekt utefter kortets id-nummer.
     * Metoden används i en studiesession för att kontrollera om  
     * ett kort redan inte har betygsatts. 
     * @param card
     * @return <br>
     * -1 ifall aktuellt objekt är mindre <br>
     * 1 ifall aktuellt objekt är större <br>
     * 0 ifall de jämförda objekten är lika stora
     */
    @Override
    public int compareTo(Card card) {
        return this.cardId - card.getCardId();
    }
}