
/**
 * CharLimiter.java
 */

package cards;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Används av StyledDocument. <br>
 * Klassen fungerar som ett filter som begränsar användaren från att skriva fler än
 * 255 tecken för varje framsida eller baksida på ett kort. Detta för att man
 * i databasen inte tillåts att lägga till ett strängvärde på fler än 255 tecken
 * då kolumnerna är skapade genom <code>varchar(255)</code>. Omvandlar även
 * < > till dess motsvarande tecken i HTML.
 * Idén till lösningen på problemet hittade jag genom att läsa detta
 * instruktionsavsnitt från Java tutorials:<br>
 * <a href="http://docs.oracle.com/javase/tutorial/uiswing/components/generaltext.html#filterText"> Text Component Features</a>
 * @author Kristian Mörling
 */
public class CharLimiter extends DocumentFilter {
    private final int maxNbrOfChars = 255; //max antal tecken per sida
    
    /**
     * Metoden blockerar användaren från att mata in fler än 255 tecken för
     * varje sida på ett kort.
     * @param fb referens till dokument
     * @param offset används ej
     * @param length längd på ny text att lägga till
     * @param text ny text att lägga till
     * @param attr används ej
     */
    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
    String text, AttributeSet attr) {
        try {
            int currCharsCount = fb.getDocument().getLength(); //storlek på nuvrande text i dokument
            
            //om nuvarande text + ny text överstiger inte maxgräns, lägg till text
            if ( (currCharsCount + 1) <= maxNbrOfChars) {
                super.replace(fb, offset, length, text, attr); //lägg till text
            }
        } catch (BadLocationException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Ett fel inträffade vid försök " +
                    "till \natt lägga till text i dokument!");
        }
    }
}
