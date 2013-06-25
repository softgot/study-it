
/**
 * DBViewer.java
 */

package database;
import cards.CardViewer;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Klassen beskriver programmets fristående tabellfönster samt annan grafik 
 * som öppnas från tabellfönstret, så som popupfönster eller fönster 
 * för att visa framsida och baksida av ett kort.
 * @author Kristian Mörling
 */
public class DBViewer extends JFrame {
    private JTable table; //tabell för innehållet i en kortsamling
    private JButton btnOpen; //knapp för att öppna kort
    private DBPopupMenu popupMenu;
    private DefaultTableModel tableModel;
    private JLabel lblNotifyLight, lblAverageLevel; //röd/grön lampa, kunskapsnivå, hjälpknapp
    private JComboBox<String> cmbBoxTables = new JComboBox<>(); //lista över kortsamlingar
    private ImageIcon //bilder för röd och grön lampa
            imageGreenLight = new ImageIcon(this.getClass().getResource("/pictures/circle_green.png")),
            imageRedLight = new ImageIcon(this.getClass().getResource("/pictures/circle_red.png"));
    private String[] tableHeaders = //kolumrubriker
            new String[] { "ID-nr", "Går ut", "Framsida", "Baksida", "Nivå" };
    
    public DBViewer(DBInput dbInput) {
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        //skapa en tom tabell, där innehållet i kolumnerna ej kan modifieras
        Object[][] columnsData = { { "", "", "", "", "" } }; 
        tableModel = new DefaultTableModel( columnsData, tableHeaders );
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //inaktivera modifiering av kolumner
            }
        };
        //bädda in tabell i ett scrollfönster och sätt regeln att endast
        //kunna välja en rad åt gången, tillåt ej förflyttning av kolumner
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(table.getPreferredSize());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        
        //initierar popupmeny med lyssnarklass som argument
        popupMenu = new DBPopupMenu(dbInput);
        
        //lyssnare för fönster och popupmeny
        addWindowListener(dbInput);
        table.addMouseListener(dbInput);
        
        setSize(650, 650);
        setResizable(false);
        
        //lägg till komponenter
        c.add(northPane(dbInput), BorderLayout.NORTH);
        c.add(southPane(dbInput), BorderLayout.SOUTH);
        c.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Metoden skapar den norra panelen i tabellfönstret.
     * Knappkomponenterna i panelen kopplas till tabellfönstrets lyssnarklass.
     * @param dbInput referens till lyssnarklass
     * @return tabellfönstrets norra panel
     */
    private JPanel northPane(DBInput dbInput) {
        JPanel northPane = new JPanel(null);
        btnOpen = new JButton("Öppna");
        JButton btnUpdate = new JButton("Uppdatera");
        JLabel lblHeader = new JLabel("Tabeller");
        
        //positionering + storlek av knappar, text och JComboBox
        /**************************************************************/
        Dimension size = btnUpdate.getPreferredSize();
        btnUpdate.setBounds( 525, 55, size.width, size.height );
        /**************************************************************/
        size = btnOpen.getPreferredSize();
        btnOpen.setBounds( 537, 20, size.width, size.height );
        /**************************************************************/
        size = lblHeader.getPreferredSize();
        lblHeader.setBounds( 48, 29, size.width, size.height );
        /**************************************************************/
        size = cmbBoxTables.getPreferredSize(); 
        cmbBoxTables.setBounds( 27, 55, size.width + 70, size.height );
        /**************************************************************/
        
        //koppla öppna och uppdatera knapp till lyssnarklass
        btnUpdate.addActionListener(dbInput);
        btnOpen.addActionListener(dbInput);
        
        //lägg till komponenter
        northPane.add(btnUpdate);
        northPane.add(btnOpen);
        northPane.add(lblHeader);
        northPane.add(cmbBoxTables);
        northPane.setPreferredSize(new Dimension(650, 100));
        return northPane;
    }
    
    /**
     * Metoden skapar den södra panelen i tabellfönstret.
     * På panelens östra sida placeras en lampa som är grön när det 
     * inte finns några utgångna kort och röd när det finns utgångna kort i 
     * samlingen. På panelens västra sida sida placeras text som visar 
     * kortsamlingens genomsnittliga kunskapsnivå.
     * @return tabellfönstrets södra panel
     */
    private JPanel southPane(DBInput dbInput) {
        //initiering av komponenter
        JPanel southPane = new JPanel();
        southPane.setLayout( new BoxLayout(southPane, BoxLayout.X_AXIS) );
        lblNotifyLight = new JLabel(imageGreenLight); //grön lampa default
        lblAverageLevel = new JLabel("Genomsnittlig kunskapsnivå på samling: ");

        //utseende
        lblAverageLevel.setPreferredSize(new Dimension(287, 50));
        lblAverageLevel.setFont(new Font("Dialog", Font.PLAIN, 10));
        
        //lägg till komponenter
        southPane.add(Box.createRigidArea(new Dimension(25, 50)));//ramavstånd väst
        southPane.add(lblAverageLevel); //label för genomsn. kunskapsnivå
        southPane.add(Box.createHorizontalGlue());
        southPane.add(Box.createRigidArea(new Dimension(15, 50)));
        southPane.add(lblNotifyLight); //label för lampa
        southPane.add(Box.createRigidArea(new Dimension(25, 50))); //ramavstånd öst
        return southPane;
    }
    
    /**
     * Metoden uppdaterar innehållet i tabellen.
     * Som argument till metoden skickas det som ska visas i tabellen
     * samt ett värde som säger ifall det finns några utgångna kort eller inte 
     * i kortsamlingen.<br><br>
     * Ifall det finns några utgångna kort så aktiveras röd lampa, annars 
     * så aktiveras grön lampa.
     * @param columnsData vektor innehållande tabelldata
     * @param isAnyCardOutdated talar om ifall det finns några utgångna kort 
     */
    public void setTableData( final Object[][] columnsData,
            final boolean isAnyCardOutdated, final double avgCardsLevel ) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                
                //lägg in data i tabell + visa genomsnittlig kunskapsnivå
                tableModel.setDataVector(columnsData, tableHeaders);
                lblAverageLevel.setText(String.format("Genomsnittlig "
                        + "kunskapsnivå på samling: %4.2f", avgCardsLevel));
                
                setLamp(isAnyCardOutdated);
            }
        });
    }
    
    /**
     * Visa grön eller röd lampa.
     */
    public void setLamp(boolean isAnyCardOutdated) {
        if (isAnyCardOutdated) {
                    lblNotifyLight.setIcon(imageRedLight);
                    lblNotifyLight.setToolTipText("Det finns utgångna kort"
                            + " i samlingen");
                } else {
                    lblNotifyLight.setIcon(imageGreenLight);
                    lblNotifyLight.setToolTipText("Det finns inga utgångna kort"
                            + " i samlingen");
                }
    }
    
    /**
     * Metoden uppdaterar listan över användarens kortsamlingar
     * samt gör tabellfönstret synligt.
     * Metoden anropas när man valt att öppna programmets fristående 
     * tabellfönster.
     * @param userTables ny lista att lägga till i <code>JComboBox</code>
     */
    public void openTable(String[] userTables) {
        cmbBoxTables.removeAllItems();
        for (String s : userTables) { //lägg till ny lista
            //visa inte användarid:et som tabellnamnen slutar på
            cmbBoxTables.addItem( s.substring( 0, s.length() - 5 ) );
        }
        setVisible(true); //visa tabellfönster
    }
    
    /**
     * Metoden används för att aktivera/inaktivera öppna knappen.
     * Är användaren gäst så inaktiveras öppna knappen. Är användaren en 
     * vanlig användare så aktiveras öppna knappen. Värde som talar om 
     * användartyp skickas som argument till metoden precis före det 
     * fristående tabellfönstret öppnas.
     * @param userIsGuest <code>boolean true</code> = gästanvändare<br>
     * <code>boolean false</code> = vanlig användare
     */
    public void toggleBtnOpen(boolean userIsGuest) {
        if (userIsGuest) {
            btnOpen.setEnabled(false);
        } else {
            btnOpen.setEnabled(true);
        }
    }
    
    /**
     * Returnerar index på den kortsamling användaren valt i listan över kortsamlingar.
     * @return index på vald kortsamling
     */
    public int getSelectedCmbBoxIndex() {
        return cmbBoxTables.getSelectedIndex();
    }
    
    /**
     * Returnerar idnumret på det kort användaren valt i tabellen.
     * @return kortid-nummer på valt kort
     */
    public int getSelectedCard() {
        return (Integer)table.getValueAt(table.getSelectedRow(), 0);
    }
    
    /**
     * Metoden kontrollerar ifall användaren valt någon rad i tabellen.
     * @return boolean som talar om ifall någon tabell valts eller ej
     */
    public boolean isAnyRowSelected() {
        //itererar igenom samtliga rader
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (table.isRowSelected(i)) {
                return true; //hittade en vald rad
            }
        }
        return false; //ingen rad har valts
    }
    
    /**
     * Öppnar dialogfönster som visar framsida och baksida på kort.
     * Metoden returnerar 0 ifall användaren valt "ok" eller 1 ifall 
     * användaren valt "avbryt".
     * @param cv fönster som visar framsida och baksida på kort
     * @return <code>JOptionPane.OK_OPTION</code> ifall användaren tryckt på ok, 
     * <code>JOptionPane.CANCEL_OPTION</code> ifall användaren tryckt på avbryt
     */
    public int showCardDialog(CardViewer cv) {
        return JOptionPane.showConfirmDialog(this, cv, "",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Metoden visar popupfönster med alternativen "uppdatera", "öppna" och 
     * "ta bort".
     * Metoden anropas när användaren tryckt på höger musknapp inuti 
     * tabellfönstret.
     * @param mouseXPos muspekarens placering i x-led
     * @param mouseYPos muspekarens placering i y-led
     */
    public void showPopupMenu(int mouseXPos, int mouseYPos) {
        popupMenu.show(table, mouseXPos, mouseYPos); 
    }
}
