
/**
 * CardCollection.java
 */

package cards;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Klassen beskriver en tabell, i programmet även kallat för en kortsamling.
 * Kortsamlingar utgörs av kort, där varje rad i en tabell representerar ett 
 * kort.
 * @author Kristian Mörling
 */
public class CardCollection {
    private ArrayList<Card> cards; //rader i tabell
    private String tableName; 
    
    /**
     * Konstruktören tar emot en lista innehållande kort och en sträng med 
     * kortsamlingens namn (dvs. tabellnamnet). Datan används för att skapa en 
     * kortsamling.
     * @param cards lista med kort
     * @param tableName namn på kortsamling
     */
    public CardCollection(ArrayList<Card> cards, String tableName) {
        this.cards = cards;
        this.tableName = tableName;
    }
    
    /**
     * Returnerar lista innehållande kortsamlingens kort.
     * @return <code>ArrayList</code>
     */
    public ArrayList<Card> getCards() {
        return cards;
    }
       
    /**
     * Returnerar namnet på kortsamlingen.
     * @return namn på kortsamling
     */
    public String getCollectionName() {
        return tableName;
    }
    
    /**
     * Returnerar antalet kort i kortsamlingen.
     * @return antal kort i kortsamling
     */
    public int getNbrOfCards() {
        return cards.size();
    }
    
    /**
     * Sökfunktion.
     * Letar efter det kortid som angivits som argument. Hittas kortet 
     * så returneras det i form av ett <code>Card</code>-objekt. <br>Ifall 
     * inte kortet hittas så returneras <code>null</code>.
     * @param cardId kortid nummer 
     * @return hittat kort, annars ifall kort inte hittats returneras <code>
     * null</code>
     */
    public Card getCard(int cardId) {
        Card card = null;
        for (Card c : cards) {
            if ( c.getCardId() == cardId ) { 
                card = c; //kort hittat
            }
        }
        return card;
    }
    
    public void addCard(Card card) {
        cards.add(card);
    }
    
    /**
     * Metoden skapar en multidimensionell array av innehållet i tabellen.
     * Den multidimensionella arrayen används för att skapa en tabell
     * i programmet. <br>Den multidimensionell arrayen skickas som argument vid 
     * injtiering eller tilldelning av <code>JTable</code>-objekt.
     * @return multidimensionell array av typen <code>Object</code>
     */
    public Object[][] getTable() {
        Object[][] tableContent = new Object[getNbrOfCards()][5]; //varje rad = 5 kolumner
        Date date; //utgångsdatum på kort
        DateFormat df = new SimpleDateFormat("dd MMM yyyy kk:mm"); //16 jan 2012 20:30
        
        //lägg till rader i multidimensionell array
        for ( int i = 0; i < cards.size(); i++ ) {
            date = new Date( cards.get(i).getExpiresAt() ); //utg. datum på kort
            tableContent[i][0] = cards.get(i).getCardId();
            tableContent[i][1] = df.format(date); //gör om ms till datumformat
            tableContent[i][2] = cards.get(i).getFrontSide();
            tableContent[i][3] = cards.get(i).getBackSide();
            tableContent[i][4] = cards.get(i).getLevel();
        }
        return tableContent;
    }
    
    /**
     * Metoden returnerar antalet kort som gått ut.
     * @return antalet kort som gått ut
     */
    public int getOutdatedCount() {
        long systemTime = System.currentTimeMillis(); //nuvarande tid i ms
        int nbrOfExpCards = 0;
        nbrOfExpCards = cards.stream().filter((c) -> ( systemTime >= c.getExpiresAt() )).map((_item) -> 1).reduce(nbrOfExpCards, Integer::sum); //sök efter utgångna kort
        //kort gått ut, om mindre än
        //eller lika med nuvarande tid
        return nbrOfExpCards;
    }
    
    /**
     * Metoden ställer in samtliga kort i samlingen som utgångna.
     */
    public void setCardsAsOutdated() {
        long currTime = System.currentTimeMillis(); //aktuell tid i ms
        cards.forEach((c) -> {
            c.setExpiresAt(currTime - 10000); //ny tid = aktuell tid - 10 sek.
        });
    }
    
   /**
    * Genererar och returnerar en kortsamling av de kort som är utgångna.
    * Kortsamlingen som returneras är av typen <code>CardCollection</code>.
    * @return kortsamling av utgångna kort 
    */
    public CardCollection getOutdatedCards() {
        ArrayList<Card> outdatedCards = new ArrayList<>();
        CardCollection cc; //samling att spara utgångna kort i
        long systemTime = System.currentTimeMillis(); 
        
        cards.stream().filter((c) -> ( c.getExpiresAt() <= systemTime )).forEachOrdered((c) -> {
            outdatedCards.add( c ); //lägg till utgånget kort
        }); //sök efter utgångna kort
        //skapa och returnera kortsamling
        cc = new CardCollection( outdatedCards, tableName ); 
        return cc;
    }
   
    /**
     * Metoden hämtar kortsamlingens genomsnittliga kunskapsnivå.
     * Kunskapsnivån return
     * @return flyttal som representerar kortsamlingens genomsnittliga
     * kunskapsnivå 
     */
    public double getAverageLevel() {
        double average = 0;
        for (Card c : cards) {
            average += c.getLevel(); //summera
        }
        average = average / cards.size();
        if (Double.isNaN(average)) { //är resultatet NaN?
            return 0; //returnera i så fall 0
        }
        return average;
    }
}
