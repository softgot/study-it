
/**
 * BooksLogo.java
 */

package studysession;
import java.awt.*;
import javax.swing.*;

/**
 * Klassen målar bilden på de tre böckerna som står staplade ovanpå varandra.
 * Bilden skalas om till storleken 140x110 px och placeras centralt i fönstret.
 * @author Kristian Mörling
 */
public class BooksLogo extends JPanel {
    Image image;
    
    /**
     * Konstruktören läser in och hämtar bildfilen.
     */
    public BooksLogo() {
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/pictures/studyindex.gif"));
    }
    
    /**
     * Metoden målar bilden i fönstret med storleken 140x110px samt placerar 
     * bilden i mitten.
     * @param g <code>Graphics</code>-objekt
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D)g;
        int x = (getSize().width / 2) - 70; //centerposition i x-led
        int y = (getSize().height / 2) - 55; //centerposition i y-led
        g2d.drawImage(image, x, y, 140, 110, this); //måla bild 
    }
}
