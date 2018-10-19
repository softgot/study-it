
/**
 * CreateUser.java
 */

package user;
import helpclasses.UserManager;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.*;

/**
 * Klassen skapar det fönster som används för att skapa en ny användare.
 * Fönstret öppnas i ett dialogfönster när man trycker på "ny användare" i
 * inloggningsfönstret.
 * @author Kristian Mörling
 */
public class CreateUser {
    private JPanel inputPanel = new JPanel(new GridLayout(2, 2)); //2 rader, label + textfält
    private JPasswordField passField = new JPasswordField(10);
    private JTextField textField = new JTextField(10);
    
    /**
     * Konstruktören skapar panel för inmatning av uppgifter på ny användare.
     * Panelen innehåller inmatningsfälten användarnamn, lösenord, namn,
     * personnummer, telefonnummer och adress.
     */
    public CreateUser() {
        //text för labels
        String[] textFieldLabels = { "Användarnamn", "Lösenord"};
        
        inputPanel.add(new JLabel("Användarnamn"));
        inputPanel.add(textField);
        inputPanel.add(new JLabel("Lösenord"));
        inputPanel.add(passField);
    }
    
    /**
     * Metoden kontrollerar så att användaren matat in uppgifter i varje fält.
     * En annan funktionalitet hos metoden är att den kontrollerar så att antalet
     * tecken i textfälten för användarnamn, lösenord, och telefonnummer inte
     * överstiger 32 tecken. Metoden kontrollerar även så att personnumret stämmer
     * och att adressen inte är fler än 255 tecken.
     * @return <br>
     * <code>boolean true</code> = ifall samtliga fält är korrekt angivna <br>
     * <code>boolean false</code> = ifall något fält inte är korrekt angivet
     */
    private boolean inputCorrect() {
        //hämta indata
        boolean res = true; //korrekt inmatning är default
        String inputSize = ""; //för att kontrollera antal tecken i textfält
        
        //kontrollerar varje fält, skulle ett fält vara ogiltigt blir res = false
        for (int i = 0; i < 2; i++) { //användarnamn + lösenord
            switch (i) {
                case 0: //användarnamn, namn, telefonnummer
                    inputSize = textField.getText();
                    //min 1 tecken, max = 32
                    res = ( (inputSize.length() >= 1) && (inputSize.length() <= 32) );
                    break;
                case 1: //lösenord
                    inputSize = new String(passField.getPassword(), 0,
                            passField.getPassword().length);
                    //min 1 tecken, max 32
                    res = ( (inputSize.length() >= 1) && (inputSize.length() <= 32) );
            }
            if (res == false) { //om ett fält är ogiltigt returnera resultatet
                return res;     //ogiltig inmatning
            }
        }
        return res;
        
    }
    
    /**
     * Metoden skapar en ny användare. Vid anrop öppnas ett dialogfönster, där
     * användaren kan mata in önskat användarnamn och lösenord.
     * @return ny användare i form av ett <code>User</code>-objekt
     * @throws SQLException när användare inte kan skapas i databas
     *
     */
    public User createUser() throws SQLException {
        JFrame frame = new JFrame("Skapa ny användare");
        
        //visa dialogfönster så länge man valt ok
        while ( (JOptionPane.showConfirmDialog(frame, inputPanel, "Skapa användare",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE ) ==
                JOptionPane.OK_OPTION ) )  {
            
            if (inputCorrect()) {
                String username = textField.getText();
                String password = new String(passField.getPassword(), 0,
                        passField.getPassword().length);
                int userId = UserManager.createUser(username, password);
                
                return new RegularUser(userId, username);
            }
            showIncorrectInputMessage();
        }
        return null;
    }
    
    /**
     * Metoden kontrollerar ifall ett personnummer är korrekt angivet.
     * Ett ogiltigt personnummer har en person som är äldre än 100 år eller
     * yngre än 5 år. Metoden kontrollerar även ifall personnumret är angivet
     * på formen yyyymmdd-xxxx. Ytterligare en kontroll som utförs är en kontroll
     * över angiven månad och dag. Skulle en månad vara utanför intervallet
     * 1-12 eller en dag utanför intervallet 1-31 så räknas personnumret som
     * ogiltigt.
     * @return <br><code>boolean true</code> = korrekt angivet personnummer
     * <br><code>boolean false</code> = felaktigt angivet personnummer
     */
    
    /**
     * Skriver ut ett meddelande som talar om för användaren att inmatade
     * uppgifter inte är korrekta.
     */
    private static void showIncorrectInputMessage() {
        JOptionPane.showMessageDialog(null, "Kontrollera så att samtliga fält är ifyllda. "
                + "\n\nKontrollera så att fälten användarnamn, lösenord, \nnamn och telefonnummer"
                + " inte överstiger 32 tecken \nsamt att adressfältet innehåller max 255 tecken.",
                "Felaktig inmatning", JOptionPane.ERROR_MESSAGE);
    }
}