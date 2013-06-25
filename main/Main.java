
/**
 * Main.java
 */

package main;
import controlcenter.ControlCenter;
import database.SQL;
import helpclasses.MenuItemGroup;
import interfaces.User;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import menubar.Menubar;
import user.Login;

/**
 * Main klassen för programmet.
 * Klassen startar programmet vid korrekt inloggning.
 * @author Kristian Mörling
 */
public class Main {
    public static JFrame frame = new JFrame();
    public static JLabel lblCurrUser = new JLabel(""), lblTimer = new JLabel("");
    private static Cursor defaultCursor;
    private Menubar menubar;
    private ControlCenter ctrlCenter;
    private Container c;
    private ButtonBar buttonBar;
    
    public Main(User user) {
//        new BooksLogo();
        new MenuItemGroup(); //initierar knappgrupper
        MenuItemGroup.setUser(user); //informerar knappgrupper om användartyp
        c = frame.getContentPane();
        buttonBar = new ButtonBar();
        menubar = new Menubar();
        ctrlCenter =  new ControlCenter(user, buttonBar, menubar);
        
        //spara muspekare
        defaultCursor = (Main.frame.isCursorSet() ? Main.frame.getCursor() : null);
        
        //lägg till komponenter
        c.add(buttonBar, BorderLayout.EAST);
        c.add(ctrlCenter, BorderLayout.CENTER);
        c.add(menubar, BorderLayout.NORTH);
        c.add(southPanel(user.getUsername()), BorderLayout.SOUTH);
        
        frame.setSize(650,500);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //windowadapter stänger program
        
        //lyssnare för att anropa avsluta metod vid tryck på fönsterramens 'x'
        frame.addWindowListener(new MainFrameListener());
        
        //aktiverar knappgrupp för aktuellt fönster
        MenuItemGroup.enableGroup(MenuItemGroup.CTRL_CENTER); 
    }
    
    /**
     * Programfönstrets södra panel.
     * Visar inloggad användare och timer.
     * @param userName inloggad användare vid programstart
     * @return panel innehållande användarens namn och en timer
     */
    private JPanel southPanel(String userName) {
        JPanel pnlSouth = new JPanel();
        pnlSouth.setLayout(new BoxLayout(pnlSouth, BoxLayout.X_AXIS));
        
        //storlek och utseende på JLabel:s för inloggad användare och timer
        lblCurrUser.setForeground(Color.DARK_GRAY);
        lblCurrUser.setFont(new Font("Dialog", Font.CENTER_BASELINE, 9));
        lblCurrUser.setText( "Inloggad som " + userName );
        lblCurrUser.setOpaque(true); //gör inte komponent genomskinlig
        lblTimer.setPreferredSize(new Dimension(52, 20));
        lblTimer.setOpaque(true);
        
        //lägg till komp. i panel
        pnlSouth.add(Box.createHorizontalGlue()); //centrerar namnobjekt
        pnlSouth.add(lblCurrUser);
        pnlSouth.add(Box.createHorizontalGlue());//centrerar namnobjekt
        pnlSouth.add(lblTimer);
        return pnlSouth;
    }
    
    /**
     * Anropas vid byte av användare för att uppdatera texten som visar vilken
     * användare som är inloggad.
     * Metoden uppdaterar programmets nedre textfält som visar vilken användare 
     * som är inloggad.
     * @param username uppdatera textfält med detta användarnamn
     */
    public static void setLoggedInUserLabel(final String username) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lblCurrUser.setText("Inloggad som " + username);
            }
        });
    }
    
    /**
     * Sätter titel på ramen till programmets huvudfönster.
     * @param title titel att byta till
     */
    public static void setFrameTitle(final String title) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setTitle(title);
            }
        });
    }
    
    public static void setWaitCursor(boolean display) {
        if (display) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            frame.setCursor(defaultCursor);
        }
    }
    
    /**
     * Metoden anropas för att avsluta programmet.
     * Om användaren valt att avsluta innan nytillagda kort har sparats, så 
     * får användaren bekräfta sitt val först. Därefter kopplas förbindelsen ned 
     * och programmet avslutas.
     */
    public static void exitProgram() {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            SQL.disconnect();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        System.exit(0);
    }
    
    /**
     * Main metoden i Main klassen startar programmet.
     * Metoden anropar klassen <code>Login</code> som returnerar ett
     * användarobjekt vid korrekt inloggning. <br><br>
     * Metoden initierar även klassen som används för att gruppera
     * programmets knapp och menyobjekt.
     * @param args används ej
     */
    public static void main(String[] args) {
        try {
            final User user = new Login().login();
            if (user != null) { //korrekt inloggning
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Main(user); //starta program
                    }
                });
            } else { //inloggningsdialog nedstängd, avsluta program
                SQL.disconnect();
                System.exit(0);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, 
                    "Kunde inte koppla upp program mot databas!", 
                    "Fel vid uppkoppling till databas", 
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}