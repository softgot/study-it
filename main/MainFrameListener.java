package main;

import adapters.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Fönsterlyssnare till programmets huvudfönster.
 * Programmet lyssnar efter tryck på huvudfönstrets 'x'-knapp. Vid tryck på 
 * denna knapp anropas Main klassens avsluta metod <code>exitProgram</code>.
 * @author kristian
 */
public class MainFrameListener extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        Main.exitProgram();
    }
}
