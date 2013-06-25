
/**
 * SSViewer.java
 */

package studysession;
import cards.Card;
import helpclasses.HTML;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;


/**
 * Klassen beskriver det fönster som presenterar korten för användaren 
 * under en kortövning.
 * @author Kristian Mörling
 */
public class SSViewer extends JPanel {
    private JPanel answerPanel; //panel för baksida på kort
    private CardLayout cardLayout = new CardLayout();
    private JLabel lblQuestion = new JLabel("", JLabel.CENTER), //labels för 
            lblAnswer = new JLabel("", JLabel.CENTER); //framsida och baksida
    private JButton btnShowAnswer = new JButton("Visa svar"),
            btnCorrect = new JButton("Rätt"), 
            btnIncorrect = new JButton("Fel");
    
    public SSViewer(SSInput input) {
        JPanel pnlParent = new JPanel();
        
        //färger på framsida och baksida på kort
        lblQuestion.setOpaque(true);
        lblQuestion.setBackground(Color.WHITE);
        lblAnswer.setOpaque(true);
        lblAnswer.setBackground(Color.WHITE);
        btnCorrect.setForeground(Color.GREEN); 
        btnIncorrect.setForeground(Color.RED);
        btnCorrect.setBackground(Color.WHITE);
        btnIncorrect.setBackground(Color.WHITE);
        
        //aktivera lyssnare för knappar & tangentbord
        btnShowAnswer.addActionListener(input);
        btnCorrect.addActionListener(input);
        btnIncorrect.addActionListener(input);
        
        //mnemonics knappar
        btnShowAnswer.setMnemonic(KeyEvent.VK_V);
        btnCorrect.setMnemonic(KeyEvent.VK_R);
        btnIncorrect.setMnemonic(KeyEvent.VK_F);

        pnlParent.setPreferredSize(new Dimension(500, 385)); //hela fönstrets storlek
        pnlParent.setLayout(new GridLayout(2, 1)); //fråga överst, svar nederst
        pnlParent.add( questionPanel() ); //lägg till fråga
        pnlParent.add( answerPanel = answerPanel() ); //lägg till svar
        pnlParent.setBorder(BorderFactory.createRaisedBevelBorder());
        
        //omge huvudpanelen med en rubrikram
        setBorder(BorderFactory.createTitledBorder(null, "Studera samling", 0, 0, 
                new Font("Serif", Font.BOLD, 22)));
        
        add(pnlParent); //lägg till framsida och baksida på kort 
    }

    /**
     * Metoden skapar panelen som visar kortets framsida (fråga).
     * @return panel med grafik för kortets framsida och knapp
     */
    private JPanel questionPanel() {
        JPanel questionPanel = new JPanel(new BorderLayout()), btnPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(lblQuestion);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        //skapa en knappanel med knapp i mitten av panelen, gör även
        //så att knappanel inte ändrar storlek när knapp görs osynlig
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(Box.createRigidArea(new Dimension(0, 30))); //fast höjd
        btnPanel.add(Box.createGlue()); //centrerar knappen
        btnPanel.add(btnShowAnswer); 
        btnPanel.add(Box.createGlue()); //centrerar knappen
        btnPanel.setBackground(Color.WHITE);
      
        questionPanel.setBackground(Color.WHITE);
        questionPanel.add(scrollPane, BorderLayout.CENTER);
        questionPanel.add(btnPanel, BorderLayout.SOUTH); //knapp "Visa svar"
        questionPanel.setBorder(BorderFactory.createMatteBorder(
                0, 0, 2, 0, Color.GRAY)); //ram mellan svarskort och frågekort
        return questionPanel;
    }
   
    /**
     * Metoden skapar panelen som visar kortets baksida (svar).
     * Metoden skapar en panel med <code>CardLayout</code>. I layouten läggs 2 
     * paneler till. Den ena panelen gömmer svaret. Den andra panelen
     * visar svaret när användaren tryckt på visa svar.
     * @return <code>CardLayout</code>-panel med grafik för kortets baksida och
     * knappar för att bedöma svar
     */
    private JPanel answerPanel() {
        JPanel ansPanel = new JPanel(cardLayout), 
                hideAnswerPanel = new JPanel(), //panel som döljer svar
                showAnswerPanel = new JPanel(new BorderLayout()); //svarspanel
        
        //bädda in svarstext i en scrollpane, dölj ram på scrollpane
        JScrollPane scrollPane = new JScrollPane(lblAnswer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        //färglägg paneler
        showAnswerPanel.setBackground(Color.WHITE);
        hideAnswerPanel.setBackground(Color.WHITE);
        ansPanel.setBackground(Color.WHITE);        
        
        //lägg till knappar + scrollpane i svarspanel
        showAnswerPanel.add(scrollPane, BorderLayout.CENTER);
        showAnswerPanel.add(sButtonPanel(), BorderLayout.SOUTH);
                
        //lägg både till panel som döljer och visar svar
        ansPanel.add(hideAnswerPanel, "HIDE_ANSWER");
        ansPanel.add(showAnswerPanel, "SHOW_ANSWER");
        return ansPanel;
    }
    
    /**
     * Metoden skapar en knappanel med knappar för att bedöma svar på kort.
     * @return knappanel med knappar för att bedöma svar på kort
     */
    private JPanel sButtonPanel() {
        JPanel sButtonPanel = new JPanel();
        sButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sButtonPanel.setBackground(Color.WHITE);
        btnIncorrect.setPreferredSize(btnCorrect.getPreferredSize()); //båda knappar samma storlek
        sButtonPanel.add(btnCorrect); sButtonPanel.add(btnIncorrect);
        return sButtonPanel;
    }
    
    /**
     * Metoden visar framsida och baksida på ett kort.
     * Baksidan på kortet visas inte förräns man tryckt på knappen "Visa svar".
     * @param card kort att visa 
     */
    public void setCard(final Card card) { 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                btnCorrect.setEnabled(true); //aktivera knappar
                btnIncorrect.setEnabled(true); //för att bedöma svar
                cardLayout.show(answerPanel, "HIDE_ANSWER"); 
                //centrering & automatiskt radbyte
                lblQuestion.setText(HTML.text2Html(card.getFrontSide()));
                lblAnswer.setText(HTML.text2Html(card.getBackSide()));
                btnShowAnswer.setVisible(true); //aktivera knapp för att visa svar
            }
        });
    }
    
    /**
     * Metoden visar svaret på kortet (baksidan).
     */
    public void showAnswer() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                btnShowAnswer.setVisible(false); //göm knapp efter tryck
                cardLayout.show(answerPanel, "SHOW_ANSWER");
            }
        });
    }
    
    /**
     * Metoden visar det fönster som visas i slutet av en kortövning.
     * En kortövning är slut när användaren besvarat samtliga kort 
     * eller när tiden tagit slut.
     */
    public void showEndSession() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lblQuestion.setText(""); //ta bort fråga
                btnCorrect.setEnabled(false); //inaktivera knappar
                btnIncorrect.setEnabled(false);
                //visa meddellande
                lblAnswer.setText("Slut på session tryck på röd knapp för "
                        + "att återvända till huvudmeny");
            }
        });
    }

}