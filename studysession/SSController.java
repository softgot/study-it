
/**
 * SSController.java
 */

package studysession;
import cards.CardCollection;
import controlcenter.ControlCenter;
import static controlcenter.ControlCenter.*;
import database.DBController;
import static helpclasses.MenuItemGroup.*;
import java.sql.SQLException;
import static javax.swing.JOptionPane.*;
import menubar.Menubar;

/**
 * Huvudklassen för den programdel som öppnar en kortsamling och startar en 
 * kortövning.
 * Knapptryck på knappen "Studera" i fönstret för val av ordning på kort och 
 * tidsbegräsning anropar denna klass som startar en kortövning. <br><br>
 * 
 * Även knapptryck i fönstret som används för att studera kort anropar metoder
 * i denna klass. 
 * @author Kristian Mörling
 */
public class SSController extends StudySession {
    private ControlCenter ctrlCenter; 
    private CardCollection outdatedCards; //utgångna kort i öppnad kortsamling
    private SSInput studyInput; //lyssnarklass
    private SSViewer studyViewer; //fönster för kortövning
    private SSConfigViewer configViewer; //fönster för
                                                          //val av ordning och tid
    /**
     * Konstruktören lägger till fönstrena för studier av kort i programmets 
     * <code>CardLayout</code>. Klassen med <code>CardLayout</code>-objektet 
     * skickas som argument till konstruktören.
     * @param ctrlCenter klass med <code>CardLayout</code>-objekt
     */
    public SSController(ControlCenter ctrlCenter, Menubar menubar,
            DBController dbCtrler) {
        super(dbCtrler);
        BooksLogo booksLogo = new BooksLogo(); //ladda bildlogga
        this.ctrlCenter = ctrlCenter;
        studyInput = new SSInput(this, menubar);
        studyViewer = new SSViewer(studyInput); 
        configViewer = new SSConfigViewer(this, booksLogo);
        
        //lägg till fönster för val av tid och ordning, samt fönster för 
        //kortövning i CardLayout-objekt 
        ctrlCenter.add(configViewer, "CONFIG_VIEWER");
        ctrlCenter.add(studyViewer, "STUDY_VIEWER"); 
    }
    
    /**
     * Metoden öppnar en kortsamling.
     * Innan metoden öppnar kortsamlingen så sparas kortsamlingens utgångna kort 
     * i en tillfällig lista. De utgångna korten i listan presenteras för 
     * användaren när användaren att studera kortsamlingen.
     */
    public void showCardCollection() {
        try {
            outdatedCards = user.getCardCollection(currCollection).
                    getOutdatedCards(); //hämta utgångna kort
            configViewer.refreshConfigViewer(currCollection); //visa namn på samling
            cardLayout.show(ctrlCenter, "CONFIG_VIEWER"); //visa GUI
        } 
        catch (NullPointerException ex) { //kastas vid sökning utan resultat
            System.out.println(ex);       //i metoden User.getCardCollection()
            showMessageDialog(null, "Kunde inte öppna kortsamling!");
        }
    }
    
    /**
     * Metoden startar en kortövning.
     * Studera knappen anropar metoden som startar en kortövning av utgångna kort. 
     * Skulle kortsamling sakna kort så visas ett meddellande. <br><br>Skulle 
     * kortsamlingen sakna utgångna kort så ges användaren förfrågan om att 
     * ställa in samtliga kort som utgångna. I annat fall, om det redan finns 
     * utgångna kort i samlingen så startar övningen.
     * @throws SQLException 
     */
    public void showStudySession() {
        try {
            boolean isCollectionEmpty = user.getCardCollection(currCollection).
                    getNbrOfCards() < 1;
            
            if (isCollectionEmpty) {
                showMessageDialog(null, "Samlingen saknar kort.\n\n"
                        + "Tryck på plusknappen eller välj nytt kort från menyraden"
                        + "\nför att lägga till kort i samlingen.\n");
                
            } else if ( outdatedCards.getNbrOfCards() < 1 ) {
                resetSessionCards(); //saknar utgångna kort
            } else {
                startSession(); //det finns utgångna kort
            }
        } catch (NullPointerException ex) { //vid sökning utan resultat 
            System.out.println(ex);         //User.getCardCollection()
            showMessageDialog(null, "Kunde inte hitta kortsamling!");
        }
    }
    
    /**
     * Hämtar användarens val och startar kortövning.
     * Metoden hämtar info om tidsbegränsning och ordning på kortpresentation, 
     * för att sedan skicka informationen som argument till superklassen
     * som startar kortövningen.
     */
    private void startSession() {
        //hämta användarens önskade inställningar
        long timeLim = configViewer.getTimeLimit();
        boolean normalOrder = configViewer.isNormalOrder();
        
        //anmäl kortövning som påbörjad och starta övning
        sessionActive = true; //används av timer
        super.startSession(studyViewer, outdatedCards.getCards(), normalOrder, 
                timeLim);
        
        enableGroup(SS_VIEWER); //aktivera knappar
        cardLayout.show(ctrlCenter, "STUDY_VIEWER"); //visa GUI för övning
    }
    
    /**
     * Återställer samtliga kort i öppnad kortsamling.
     * Först ges användaren förfrågan om att återställa korten.
     */
    private void resetSessionCards() {
        String message = "Det finns inga utgånga kort. \n\n"
                + "Vill du återställa utgångstiden på samtliga kort?\n";
        
        if (((showConfirmDialog(null, message, "", YES_NO_OPTION)) == YES_OPTION)){
            
            try {
                //ställer in samtliga kort i samling som utgångna
                user.getCardCollection(currCollection).setCardsAsOutdated();
                
                showMessageDialog(null,
                        "Utgångstiden på samtliga kort är nu återställd. " +
                        "\n\nTryck på studera för att studera samlingen.\n");
                
                showCardCollection(); //återöppna kortsamling
            }
            catch (NullPointerException ex) {
                System.out.println(ex);
                showMessageDialog(null, "Misslyckades med att återställa \n"
                        + "utgångstiden på samtliga kort! \n\n"
                        + "Kunde inte hitta kortsamlingen.");
            }
        }
    }
}