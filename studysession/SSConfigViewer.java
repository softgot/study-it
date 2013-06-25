
/**
 * SSConfigViewer.java
 */

package studysession;
import java.awt.*;
import javax.swing.*;

/**
 * Klassen beskriver det fönster som låter användaren bestämma tidsbegränsning 
 * och ordningen på korten inför en övning av en kortsamling.
 * Klassen innehåller även metoder som används för att ge information om vilka 
 * val användaren gjort.
 * @author Kristian Mörling
 */
public class SSConfigViewer extends JPanel {
    private JComboBox<Long> cmbBoxTime = new JComboBox<>( //combobox tidsbegränsning
            new Long[]{ 0L, 2L, 5L, 10L, 20L } );
    private JComboBox<String> cmbBoxOrder = new JComboBox<String>(
            new String[]{"Vanlig", "Blandad"}); //combobox kortordning
    private JLabel lblCollection = new JLabel("", JLabel.CENTER);

    /**
     * Konstruktören initierar klassens grafiska komponenter. 
     * @param controller 
     */
    public SSConfigViewer(SSController controller, BooksLogo booksLogo) {
        //panel för att placera combobox, text och knapp i 
        JPanel pnlParent = new JPanel(new GridLayout(2, 1, 0, 35));
        
        //storlek och ram
        pnlParent.setPreferredSize( new Dimension(500, 385) );
        pnlParent.setBorder(BorderFactory.createRaisedBevelBorder()); //upphöjd ram
        
        //lägg till södra och norra panelen
        pnlParent.add(northPanel(booksLogo));
        pnlParent.add(southPanel(controller));

        //lägg till ram med rubrik i huvudpanelen, 
        //lägg sedan till panel för komponenter i huvudpanelen
        setBorder(BorderFactory.createTitledBorder(null, "Studera samling", 0, 0, 
                new Font("Serif", Font.BOLD, 22)));
        add(pnlParent);   
    }
    
    /**
     * Metoden skapar fönstrets norra panel med rubriktext över aktuell 
     * samling och bild med böcker.
     * @return panel med rubriktext och bild
     */
    private JPanel northPanel(BooksLogo booksLogo) {
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
//        lblCollection.setOpaque(true); //måla varje pixel i rubriken
//        lblCollection.setFont(new Font("Serif", Font.ITALIC, 18));
        
        //gör mindre pixlig
        lblCollection.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 20));
        lblCollection.setOpaque(true);
        
        northPanel.add(booksLogo, BorderLayout.CENTER); //lägg till bild
        northPanel.add(lblCollection, BorderLayout.NORTH); //lägg till text
        return northPanel;
    }
    
    /**
     * Metoden skapar den södra panelens rullistor för val av tidsbegränsning 
     * och val av ordning på kort, samt knapp för att starta repetition av 
     * öppnad kortsamling.
     * @param controller klass som anropas vid knapptryck
     * @return panel med rullistor för tid och ordning och studera knapp
     */
    private JPanel southPanel(SSController controller) {
        //skapa ett vertikalt avstånd på 10px mellan komponenter
        JPanel southPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        JPanel cmbBoxPanel = new JPanel();
        JPanel btnPanel = new JPanel();
        JButton btnStudyCards = new JButton("Studera");
        
        //panel med combobox
        cmbBoxPanel.add(arrangementPanel()); //combobox för val av ordning på kort
        cmbBoxPanel.add(new JLabel("")); //mellanrum
        cmbBoxPanel.add(new JLabel("")); //mellanrum
        cmbBoxPanel.add(new JLabel("")); //mellanrum
        cmbBoxPanel.add(timePanel()); //combobox för val av tid
        
        //koppla studeraknapp till lyssnarklass
        btnStudyCards.addActionListener(new SSConfigInput(controller));
        
        btnPanel.add(btnStudyCards); //lägg till knapp i knappanel
        southPanel.add(cmbBoxPanel); //lägg till tid och kortordnings-panel
        southPanel.add(btnPanel); //lägg till knappanel
        return southPanel;
    }
    
    /**
     * Används för att skapa en panel med text som beskriver rullista och 
     * en rullista för val av ordning på kort.
     * Metoden används av metoden <code>southPanel</code>.
     * @return panel med text och rullista för val av ordning på kort
     */
    private JPanel arrangementPanel() {
        JPanel pnlArr = new JPanel(new BorderLayout());
        JLabel lblArrangement = new JLabel("Ordning", JLabel.CENTER);
        
        pnlArr.add(cmbBoxOrder, BorderLayout.CENTER); //lägg till rullsita
        pnlArr.add(lblArrangement, BorderLayout.NORTH); //lägg till rubrik
        return pnlArr;
    }
    
    /**
     * Används för att skapa en panel med text som beskriver rullista och en 
     * rullista för val av tidsbegränsning.
     * Metoden används av metoden <code>southPanel</code>.
     * @return panel med text och rullista för val av tidsbegränsning 
     */
    private JPanel timePanel() {
        JPanel pnlTime = new JPanel(new BorderLayout());
        JLabel lblTime = new JLabel("Tid i minuter", JLabel.CENTER);
        
        pnlTime.add(lblTime, BorderLayout.NORTH); //lägg till text
        pnlTime.add(cmbBoxTime, BorderLayout.CENTER); //lägg till rullista
        return pnlTime;
    }
    
    /**
     * Metoden uppdaterar rubriktexten som beskriver vilken kortsamling 
     * användaren öppnat.
     * @param collectionName kortsamling användaren öppnat
     */
    public void refreshConfigViewer(final String collectionName) {  
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //visa tabellnamn utan de fem sista tecknena då tabellnamn 
                //slutar på ex. _1001 (användarid)
                lblCollection.setText(collectionName.substring(0, 
                        collectionName.length() - 5));
            }
        });
    }
    
    /**
     * Kontrollerar ifall användaren valt att få korten presenterade i vanlig 
     * ordning eller i blandad ordning.
     * @return <code>boolean true</code> = vanlig ordning <br>
     * <code>boolean false</code> = blandad ordning
     */
    public boolean isNormalOrder() {
        return cmbBoxOrder.getSelectedIndex() == 0;
    }
    
    /**
     * Returnerar den tidsgbegränsning användaren valt.
     * Eftersom programmets timer användaren tidsenheten millisekunder så 
     * returnerar tidsbegränsningen angiven i millisekunder. Ett val med en 
     * tidsbegränsning på 0 startar inte timern.
     * @return tidsbegränsning inom intervallet 0 - 20 minuter angivet i ms.
     */
    public long  getTimeLimit() {
        return cmbBoxTime.getItemAt(cmbBoxTime.getSelectedIndex()) * 60000;
    }
}