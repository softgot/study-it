
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
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public abstract void windowClosing(WindowEvent e);
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}
