
/**
 * StudySession.java
 */

package studysession;
import cards.Card;
import database.DBController;
import helpclasses.ExpireManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Klassen simulerar en kortövning.
 * Klassen simulerar en kortövning genom att skicka nya kortobjekt till fönster
 * för kortövning. Klassen kontrollerar även ifall inte tiden gått ut, uppdaterar
 * utgångstiden och kunskapsnivå på varje kort, avslutar en kortövning, visar 
 * fråga, visar svar, blandar om ordningen på korten och startar en kortövning.
 * @author Kristian Mörling
 */
public class StudySession {
    private Card currCard; 
    private long timeLimit;
    private SSViewer viewer;
    private StudyTimer timer;
    private DBController dbCtrler; //för att anropa databas vid menytryck "Redigera kort"
    private LinkedList<Card> gradedList; //används för att hålla reda på vilka kort som har betygsatts
    private ArrayList<Card> sessionQueue;
    
    public StudySession(DBController dbCtrler) {
        this.dbCtrler = dbCtrler;
    }
    
    /**
     * Metoden bestämmer nästa utgångstid och kunskapsnivå på besvarat kort.
     * Metoden lägger även till felsvarade kort längst bak i kön, så att 
     * användaren får repetera kortet emot slutet av kortövningen. Metoden 
     * kontrollerar även ifall inte ett kort redan har betygsatts, för att inte
     * betygsätta ett kort fler än 1 gång.
     * @param correctAnswer
     */
    private void setAnswer(boolean correctAnswer) {
        //kontrollera ifall kort inte redan betygsatts
        Collections.sort(gradedList); //binarysearch kräver sorterad lista
        boolean inGradedList = (Collections.binarySearch(gradedList, currCard) >= 0);
        
        if (!inGradedList) {
            //uppdatera utgångstid och kunskapsnivå på kort, 
            //lägg till i lista över betygsatta kort
            ExpireManager.setExpiresAt(currCard, correctAnswer); 
            gradedList.add(currCard);
            
            if (!correctAnswer) {
                //vid felaktigt svar, lägg tillbaks kort längst bak i kö för 
                //att studera igenom emot slutet
                sessionQueue.add(sessionQueue.size(), currCard);
            }
        }
    }
    
    /**
     * Metoden visar nästa kort.
     * Metoden anropas från lyssnarklass som skickar med ett <code>boolean</code>
     * -värde som argument. <br>
     * <code>true</code> = användaren svarat rätt på föregående fråga <br>
     * <code>false</code> = användaren svarat fel på föregående fråga <br>
     * Värdena används för att bestämma nästa utgångsdatum och kunskapsnivå på 
     * nyligen besvarat kort.
     * @param correctAnswer resultat på nyligen besvarat kort
     */
    public void nextCard(boolean correctAnswer) {
        //bestäm nytt utgångsdatum och kunskapsnivå på nyligen besvarat kort
        setAnswer(correctAnswer);
        
        //visa nästa kort om kort finns och tiden inte gått ut
        if ( (!sessionQueue.isEmpty()) && (!isTimeOver()) ) {
            currCard = sessionQueue.remove(0);
            viewer.setCard(currCard);
        } else { //slut på tid eller slut på kort, visa avslutningsfönster
            viewer.showEndSession();
        }
    }
    
    /**
     * Visa svar på fråga (kortets baksida).
     */
    public void showAnswer() {
        viewer.showAnswer();
    }
    
    /**
     * Startar kortövning.
     * Inställningar, kortsamling och fönster för kortövning skickas som argument 
     * från subklassen. 
     * @param viewer fönster att visa kortövning i 
     * @param sessionQueue samling med utgångna kort
     * @param normalOrder blanda om korten eller ej
     * @param timeLim tidsbegränsning användaren valt
     */
    public void startSession(SSViewer viewer, ArrayList<Card> sessionQueue,
            boolean normalOrder, long timeLim) {
        this.viewer = viewer; //fönster för kortövning
        this.sessionQueue = sessionQueue; //kort att studera
        timeLimit = timeLim; //tidsbegränsning angivet i ms
        gradedList = new LinkedList<Card>(); //init. lista över betygsatta kort
        
        if (!normalOrder) {
            //blanda om kort
            shuffleCards();
        }
        if (timeLimit > 0) {
            //starta timer 
            startTimer();
        }
        
        //visa första kort i kön
        currCard = sessionQueue.remove(0); 
        viewer.setCard(currCard);
    }
    
    /**
     * Metoden blandar om korten i kortsamlingen.
     */
    public void shuffleCards() {
        Collections.shuffle(sessionQueue);
    }
    
    /**
     * Startar timern ifall man valt en tidsbegränsning > 0.
     */
    public void startTimer() {
        timer = new StudyTimer(timeLimit);
    }
    
    /**
     * Kontrollerar ifall tid för kortövning tagit slut.
     * Har användaren inte valt någon tidsbegränsning så returneras
     * <code>false</code>.
     * @return <code>boolean true</code> = tid tagit slut <br>
     * <code>boolean false</code> = tiden har inte tagit slut
     */
    public boolean isTimeOver() {
        if (timeLimit > 0) { 
            //kontrollera ifall tid tagit slut
            return timer.isTimeOver(); 
        } else {
            //timer ej aktiverad, det finns tid över
            return false; 
        }
    }
    
    /**
     * Metoden används för att redigera det kort som visas i studiefönstret.
     */
    public void editCurrentCard() {
        dbCtrler.editCurrentSessionCard(currCard);
        viewer.setCard(currCard); //uppdatera så att förändringar syns i kort som studeras
    }
}
