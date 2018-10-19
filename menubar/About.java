
/*
 * About.java
 */

package menubar;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Klassen beskriver dialogfönstret som visar Malmö högskolas logo tillsammans 
 * med programmets namn.
 * @author Kristian Mörling
 */
public class About implements ActionListener {
    private static JDialog dialog = new JDialog();
    
    /**
     * Konstruktören initierar fönstrets komponenter.
     */
    public About() {
        String path = "/pictures/"
                + "studyit.gif"; //sökväg till bildfil
        JPanel pnlSouth = new JPanel();
        JLabel lblLogo  = new JLabel( new ImageIcon( this.getClass().getResource(path ) ) ); //hämta bild
        JButton btnClose = new JButton( "Stäng" );
        
        lblLogo.setOpaque(true); //visa varje pixel
        dialog.setModal(true); //lås inte programmet när fönster öppnas
        dialog.setLayout( new BorderLayout() );
        
        btnClose.addActionListener(this); //koppla lyssnare till knapp
        
        //placera knapp i mitten av den södra panelen
        pnlSouth.add(btnClose, JPanel.CENTER_ALIGNMENT ); 
        
        //lägg till komponenter
        dialog.add( lblLogo );
        dialog.add( pnlSouth, BorderLayout.SOUTH );
        dialog.pack(); //anpassa fönsterstorlek till komponenter
    }
    
    /**
     * Metoden anropas för att visa dialogfönstret.
     */
    public static void showAbout() {
        dialog.setVisible(true); 
    }

    /**
     * Tryck på stängknappen anropar metoden som stänger ned dialogfönstret.
     * @param ae knapphändelse
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        dialog.setVisible(false);
    }
}
