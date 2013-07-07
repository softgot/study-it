/*
 * Klassen används för att konvertera text till HTML och vice versa.
 * Innehåller även en metod som kontrollerar ifall text innehåller HTML syntax.
 * Klassen kommer till användning vid redigering och skapande av kort.
 * Eftersom korten visas i en <code>JLabel</code> så måste texten konverteras
 * till HTML.
 */
package helpclasses;

/**
 * @author Kristian Mörling
 */
public class HTML {
    private static String[] tags = {  "&lt", "&gt", "<br>", "&nbsp;&nbsp;&nbsp;" };
    private static String[] ctrlCharacters = { "<", ">", "\n", "\t" };
    
    /**
     * Returnerar vanligt text som html text.
     * @param text - vanlig sträng att konvertera
     * @return - vanlig text konverterad till HTML-text
     */
    public static String text2Html(String text) {
        for (int i = 0; i < tags.length; i++) {
            text = text.replaceAll(ctrlCharacters[i], tags[i]);
        }
        text = "<html><body style='width: 300px'><center>" + text;
        return text;
    }
}

