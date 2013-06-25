
/**
 * ExpireManager.java
 */

package helpclasses;
import cards.Card;

/**
 * Klassen ställer in utgångsdatum och kunskapsnivå på ett kort.    
 * 
 * Ett kort kan ha en kunskapsnivå inom intervallet 1-8. Ett nytt kort har nivå 1. 
 * Skulle man svara rätt på ett kort så ökar kunskapsnivån på kortet med 1. 
 * Svarar man fel så sänker man kunskapsnivån på kortet med 1.<br><br>
 *   
 * Kortets nivå används för att bestämma nästa gång användaren ska få 
 * förfrågan om att studera kortet. De olika dagarna är 1, 2, 4, 8, 16, 32, 64, 128. 
 * Ett kort på nivå 1 ger användaren förfrågan om att studera kortet inom 1 dag, 
 * nivå 2: 2 dagar, nivå 3: 4 dagar osv.<br><br>
 *       
 * I databasen sparas kunskapsnivån på varje kort under kolumnen levels. Namnet 
 * levels kan bli förvirrande om man ser dem som olika svårighetsgrader. Tanken är 
 * snarare att levels ska representera den kunskapsnivå man nått. Ju fler korrekta 
 * svar desto fler levels, kunskapsnivåer ökar kortet med.<br><br>
 * @author Kristian Mörling
 */
public class ExpireManager {
    final static long[] days = 
    { 0, //0 används ej utan har endast lagts till för att placera tiderna ett steg längre fram i arrayen 
        
        /* Kunskapsnivå                           dagar i ms
        /* LEVEL 1 == 1 dag     */                86400000L, 
        /* LEVEL 2 == 2 dagar   */                172800000L, 
        /* LEVEL 3 == 4 dagar   */                345600000L, 
        /* LEVEL 4 == 8 dagar   */                691200000L,
        /* LEVEL 5 == 16 dagar  */                1382400000L, 
        /* LEVEL 6 == 32 dagar  */                2764800000L, 
        /* LEVEL 7 == 64 dagar  */                5529600000L, 
        /* LEVEL 8 == 128 dagar */                11059200000L 
    };
    
    /**
     * Metoden uppdaterar kunskapsnivå och utgångsdatum på ett kort.
     * Som argument skickas kortet samt en <code>boolean</code> som talar 
     * om ifall man svarat rätt på kortet eller ej. Ny utgångstid på kort
     * blir nuvarande tid i ms + de antal dagar kortets nya level motsvarar.
     * <br><br>Level 1 motsvarar 1 dag, level 2 motsvarar 2 dagar, level 3 motsvarar
     * 4 dagar osv.
     * @param card kort att uppdatera utgångsdatum och kunskapsnivå på
     * @param correct boolean som är true vid korrekt svar och false vid felaktigt svar
     */
    public static synchronized void setExpiresAt(Card card, boolean correct) {
        int level = card.getLevel();
        long currTime = System.currentTimeMillis(), res;
        
        //Ändra endast utgångsdatum. I de fall då man svarat fel och redan är 
        //på nivå 1. Eller man svarat rätt och redan är på nivå 8.
        //Man kan ju inte hamna utanför intervallet 1-8.
        if ( (!correct) && (level == 1) ) { //på level 1 och fel svar = level 1
            res = currTime + days[ 1 ]; 
        } else if ( (correct) && (level == 8) ) { //på level 8 och rätt svar = level 8
            res = currTime + days[ 8 ]; 
        } 
        
        //ändra utgångsdatum och kunskapsnivå
        else { //fel svar = ner en level, rätt svar = upp en level
            
            if (correct) { //rätt svar
                
                level = level + 1; //gå upp en level
                card.setLevel(level); 
                
                //sätt det utgångsdatum på kort som kunskapsnivån representerar
                res = days[ level ]; 
                res += currTime;
            } 
            
            else { //fel svar
                
                level = 1; //gå till level 1
                card.setLevel(level); 
                
                //sätt det utgångsdatum på kort som kunskapsnivån representerar
                res = days[ level ]; 
                res += currTime;
            }
        }
        card.setExpiresAt(res); //spara ny utgångstid i kort
    }
    
    /**
     * Metoden ändrar kunskapsnivån samt bestämmer det utgångsdatum som motsvarar
     * kunskapsnivån.
     * Används när man valt att ändra ett korts kunskapsnivå i det fristående 
     * tabellfönstret. 
     * @param card kort att uppdatera utgångsdatum och kunskapsnivå på
     * @param level kunskapsnivå att uppdatera till, intervall 1-8
     */
    public static void setLevel(Card card, int level) {
        long currTime, expiresAt;
        if ( (level >= 1) && (level <= 8) ) { //intervall kunskapsnivå, 1-8
            currTime = System.currentTimeMillis();
            expiresAt = currTime + days[level]; //bestäm ny utgångstid
            card.setExpiresAt(expiresAt); //spara ny utgångstid till kortobjekt
            card.setLevel(level); //spara ny nivå på kort
        }
    }
}
