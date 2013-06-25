
/**
 * CardViewer.java
 */

package cards;
import java.awt.*;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Klassen används för att visa, redigera och skapa ett kort.
 * Varje sida på ett kort får högst innehålla 255 tecken. Klassen tar hand om 
 * detta genom att inte tillåta inmatning av fler tecken än maxgränsen. 
 * @author Kristian Mörling
 */
public class CardViewer extends JPanel {
    private JTextPane textPaneFront = new JTextPane(), 
            textPaneBack = new JTextPane();

    /**
     * Konstruktören används vid konstruktion av ett nytt kort.
     * Konstruktören skapar ett nytt kort utan något innehåll.
     */
    public CardViewer() {
        this(new Card(0, "", ""));
    }
    
    /**
     * Konstruktören öppnar det kort som skickas som argument.
     * @param card kort att visa
     */
    public CardViewer(Card card) {
        setLayout(new GridLayout(2, 1)); //2 rader, framsida överst, baksida underst
        
        setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); //insets

        add(northOrSouthPanel("Framsida", textPaneFront)); //norra panelen
        add(northOrSouthPanel("Baksida", textPaneBack)); //södra panelen

        //M-z och M-x byter till respektive textfönster
        textPaneFront.setFocusAccelerator('z');
        textPaneBack.setFocusAccelerator('x');
        
        textPaneFront.setText(card.getFrontSide()); //lägg till framside- 
        textPaneBack.setText(card.getBackSide()); //och baksidetext i 
                                                 //resp. textfönster
        
        //placera textpekare i översta textfönster
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                textPaneFront.requestFocusInWindow();
            }
        };
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(timerTask, 1000);
    }                               
    
    /**
     * Initierar de regler som gäller för varje textfönster.
     * Pekaren i textfönstret är centrerad och det maximala antalet
     * tecken för varje textfönster är 255 tecken.
     * @param textPane textfönster 
     */
    private void initDocumentRules(JTextPane textPane) {
        //stildokument som centrerar pekaren
        StyledDocument styledDocument = new DefaultStyledDocument();
        Style style = styledDocument.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
        textPane.setDocument(styledDocument); 
        textPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        //dokumentfilter som hindrar anv. från att skriva fler än 255 tecken 
        ((AbstractDocument)styledDocument).
                setDocumentFilter(new CharLimiter());
    }
    
    /**
     * Panelen används för att både skapa den norra panelen (kortets framsida)
     * och den södra panelen (kortets baksida).
     * Som argument skickas rubrik, dvs. "Framsida" eller "Baksida" samt 
     * textfönstret som ska läggas till i panelen. <br>Ett anrop skapar en 
     * av kortets sidor, därför anropar konstruktören metoden 2 gånger.
     * @param hdrText rubriktext att lägga till i panel
     * @param textPane textfönster att lägga till i panel
     * @return panel innehållande textfönster och rubrik
     */
    private JPanel northOrSouthPanel(String hdrText, JTextPane textPane) {
        JPanel noOrSoPane = new JPanel(new BorderLayout());
        //skapa vertikalt avstånd mellan framsida och baksida
        noOrSoPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        noOrSoPane.add(headerLabel(hdrText), BorderLayout.NORTH); //lägg till rubrik
        noOrSoPane.add(textPanel(textPane), BorderLayout.CENTER); //lägg till textföns.
        return noOrSoPane;
    }
    
    /**
     * Skapar rubriktexten för den norra eller den södra panelen.
     * @param hdrText rubriktext
     * @return en <code>JLabel</code> innehållande rubriktext
     */
    private JLabel headerLabel(String hdrText) {
        JLabel lblHeader = new JLabel(hdrText, JLabel.CENTER);
        lblHeader.setFont(new Font("Serif", Font.PLAIN, 22));
        //skapa vertikalt avstånd mellan rubrik och textpanel
        lblHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        return lblHeader;
    }
    
    /**
     * Skapar den panel som textfönstret placeras i.
     * @param textPane textfönster att placera i panel
     * @return panel innehållande textfönster
     */
    private JPanel textPanel(JTextPane textPane) {
        JPanel textPanel = new JPanel();
        textPanel.setLayout( new BoxLayout( textPanel, BoxLayout.X_AXIS ) );
        
        //textfönster storlek
        Dimension size = new Dimension(300, 150); 
        textPane.setPreferredSize(size);
        
        //lägg till komponenter
        size = new Dimension(50, textPane.getPreferredSize().height);
        textPanel.add(Box.createRigidArea(size)); //avstånd mellan ram och textfönster, väst
        textPanel.add(new JScrollPane(textPane)); //lägg till textarea
        textPanel.add(Box.createRigidArea(size)); //avstånd mellan ram och textarea, öst
        
        initDocumentRules(textPane); //sätt dokumentregler för textfönster
        return textPanel; //returnera textfönster
    }
    
    /**
     * Metoden returnerar kortets framsidetext.
     * @return kortets framsidetext
     */
    public String getTextFront() {
        return textPaneFront.getText();
    }
    
    /**
     * Metoden returnerar kortets baksidetext.
     * @return kortets baksidetext
     */
    public String getTextBack() {
        return textPaneBack.getText();
    }
}
