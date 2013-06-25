
/**
 * WindowAdapter.java
 */

package adapters;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * WindowAdapter används för att lyssna efter försök till att avsluta programmet
 * genom 'x' knappen i programmets huvudfönster.
 * @author Kristian Mörling
 */
public abstract class WindowAdapter implements WindowListener {
    public void windowActivated(WindowEvent e) {}
    public abstract void windowClosing(WindowEvent e);
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
}
