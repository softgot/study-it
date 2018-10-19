
/**
 * Login.java
 */

package user;
import database.SQL;
import helpclasses.EnvironmentVariables;
import helpclasses.LoginManager;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * Klassen används när man skapar användare, när man loggar in för första gången
 * eller när man byter användare. 
 * @author Kristian Mörling
 */
public class Login extends JPanel {
    private User user; //returneras vid lyckad inloggning
    private JButton btnNewUser = new JButton("Ny användare");
    private JComboBox<Object>cmbBoxUsers = new JComboBox<Object>(); //rullista med användare
    private JPasswordField passwordField = new JPasswordField(10);
    
    /**
     * Konstruktören kopplar upp programmet mot databasen, initierar rullista 
     * över tillgängliga användare samt initierar de grafiska komponenterna för 
     * inloggningsfönstret.
     * @throws SQLException 
     */
    public Login() throws SQLException {
        super(new BorderLayout());
        
        //hämta absolut sökväg till databas och öppna databas
        String dbPath = EnvironmentVariables.getDbDir();
        SQL.connect(dbPath);
        
        setCmbBoxUsers(); //lägg till samtliga användare i ComboBox

        //koppla rullista och "ny användare" knapp till lyssnare
        cmbBoxUsers.addActionListener(new CmbBoxAndBtnListener());
        btnNewUser.addActionListener(new CmbBoxAndBtnListener()); 
        
        //lägg till komponenter
        add(usersDataPanel(), BorderLayout.NORTH); 
        add(buttonPanel(), BorderLayout.SOUTH);
        add(Box.createRigidArea(new Dimension(250, 150)));
    }
    
    /**
     * Metoden skapar en panel med lösenordsfält, rullista för att välja 
     * användare samt textfält för personummer, adress och telefonnummer.
     * @return panel med lista över användare och lösenordsfält
     */
    private JPanel usersDataPanel() {
        //användarnamn och lösenordsobjekt
        JPanel userPasswordPanel = new JPanel(new GridLayout(2, 2));
        userPasswordPanel.add(new JLabel("Användarnamn "));
        userPasswordPanel.add(cmbBoxUsers);
        userPasswordPanel.add(new JLabel("Lösenord "));
        passwordField.setVisible(false);
        userPasswordPanel.add(passwordField);
        return userPasswordPanel;
    }
    
    /**
     * Metoden skapar loginfönstrets södra knappanel med knappen "Ny användare".
     * @return knappanel med knappen "Ny användare"
     */
    private JPanel buttonPanel() {
        JPanel buttonPanel = new JPanel();
        btnNewUser.setBorder(null); //visa inte knappram
        buttonPanel.add(btnNewUser);
        return buttonPanel;
    }
    
    /**
     * Metoden används för att lägga till användare från databasen i en rullista.
     * Metoden gör det möjligt att ha flera användare med samma namn i samma 
     * rullista, så länge användar-id:ena mellan användarna inte är identiska.
     * @throws SQLException 
     */
    private void setCmbBoxUsers() throws SQLException {
        int userId;
        String userName = "";
        String[][] usersNameAndId = LoginManager.getUsers(); //hämta anv. från db
        DefaultComboBoxModel<Object> cmbBoxModel = new DefaultComboBoxModel<Object>();
        
        //töm lista, lägg till samtliga användare
        cmbBoxUsers.removeAllItems();
        for (int i = 0; i < usersNameAndId.length; i++) {
            userId = Integer.parseInt( usersNameAndId[ i ][ 0 ] );
            userName = usersNameAndId[ i ][ 1 ];
            cmbBoxModel.addElement( new BoxItem( userId, userName ) );
        }
        cmbBoxUsers.setModel(cmbBoxModel); //lägg till användare
        //gästanvändare väljs automatiskt, inaktivera lösenordsfält
        passwordField.setVisible(false);
    }
    
    /**
     * Metoden kontrollerar ifall inloggningsuppgifterna stämmer.
     * @return boolean: true - korrekt inlogging, false - inkorrekt inloggning
     * @throws SQLException 
     */
    private boolean accessGranted() throws SQLException {
        int selectedUid;
        BoxItem boxItem;
        boolean accessGranted = false;
        String selectedPass;
        
        if (passwordField.isVisible()) { //inlogging av användare (inte gästkonto)
            
            //hämta valt användarnamn, användarid och lösenord
            boxItem = (BoxItem)cmbBoxUsers.getSelectedItem(); 
            selectedUid = boxItem.getSelectedUserId();
            selectedPass = new String(passwordField.getPassword(), 0,
                    passwordField.getPassword().length);
            if (LoginManager.isLoginCorrect(selectedUid, selectedPass)) {
                user = new RegularUser(selectedUid, selectedPass);
                accessGranted = true;
            }
            
        } else { //inloggning av gästkonto
            user = new Guest(); //skapa gästanvändare
            accessGranted = true;
        }
        return accessGranted;
    }
    
    /**
     * Metoden öppnar ett dialogfönster som används för att logga in användare.
     * Returnerar användare ifall inloggning lyckades. Vid misslyckad inloggning
     * returneras <code>null</code>.
     * @return användarobjekt vid lyckad inloggning, annars returneras 
     * <code>null</code>
     * @throws SQLException 
     */
    public User login() throws SQLException {
        JFrame frame = new JFrame("Logga in");
        do {
            if( JOptionPane.showConfirmDialog(frame, this, "Logga in",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                    != JOptionPane.OK_OPTION ) {
                return null; //användare stängde ner dialogfönster
            }
            if ( accessGranted() ) { 
                //lyckad inloggning returnera användare
                return user;
            }
            //felaktigt angivna uppgifter, visa meddellande
            JOptionPane.showMessageDialog(null, "Åtkomst nekad!");
        } while( true );
    }
    
    /**
     * Metoden skapar en ny användare.
     * Vid anrop öppnas ett dialogfönster, där användaren kan mata in önskat
     * användarnamn, lösenord, personnummer, namn, adress och telefonnummer.
     * @return Om användare skapats så returneras ett användarobjekt av 
     * typen <code>User</code> <br>I annat fall om användaren stängt
     * ner dialogfönstret så returneras <code>null</code>
     * @throws SQLException 
     */
    public static User newUser() throws SQLException {
        return new CreateUser().createUser();
    }
    
    /**
     * Klassen beskriver elementen i ett <code>DefaultComboBoxModel</code>-objekt.
     * Elementen används i JComboBox:n som visar databasens användare. Genom
     * att använda en egen definierad rullista så är det möjligt att ha två eller
     * fler användare med identiska namn i en och samma rullista. Så länge id
     * numret på användarna inte är identiska.
     */
    private class BoxItem {
        private final int userId;
        private final String userName;
        
        public BoxItem(int userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
        
        public int getSelectedUserId() {
            return userId;
        }
        
        //visa både användarnamn och användarid i rullista över användare
        @Override
        public String toString() {
            return userName + " (" + userId + ")";
        }
    }
    
    /**
     * Lyssnarklass för knapp och rullista.
     */
    private class CmbBoxAndBtnListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                
                if (e.getSource() == cmbBoxUsers) {
                    
                    if ( cmbBoxUsers.getSelectedIndex() == 0 ) {
                        //inaktivera lösenordsfält när gästanvändare är vald
                        passwordField.setVisible(false);
                    } else {
                        //aktivera lösenordsfält när gästanvändare inte är vald
                        passwordField.setVisible(true);
                    }
                } else if (e.getSource() == btnNewUser) {
                    //öppna fönster för att skapa användare 
                    //vid tryck på knappen "Ny användare"
                    newUser();
                    setCmbBoxUsers(); //till ny användare i rullista 
                }
                
            } catch (SQLException ex) { 
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, "Ett fel inträffade när "
                        + "programmet försökte \n hämta användarlista "
                        + "eller när programmet försökte skapa en ny användare.");
            }
        }
    }
}
