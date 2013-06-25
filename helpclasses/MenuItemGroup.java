
/**
 * MenuItemGroup.java
 */

package helpclasses;
import interfaces.User;
import java.util.*;
import javax.swing.*;
import user.RegularUser;

/**
 * Klassen grupperar knapparna i programmet.
 * Syftet med klassen är att gäst användare inte ska samma tillgång till programmets
 * olika funktioner som en vanlig användare. Klassen inaktiverar även vissa 
 * knappar beroende på vilket fönster man befinner sig i. <br><br>
 * T.ex. när man öppnar det fristående tabellfönstret så ska man inte samtidigt 
 * kunna studera kort. Detta skulle kunna leda till problem då både tabellfönstret 
 * och studier av kort kommunicerar med databasen. <br><br>
 * 
 * Varje knappgrupp består av en ArrayList. Vid byte av fönster inaktiveras först
 * samtliga knappgrupper, därefter aktiveras den grupp knappar som ska vara 
 * aktiverade för det fönster man befinner sig i.<br><br> 
 * @author Kristian Mörling
 */
public class MenuItemGroup {
    public static final int SS_VIEWER = 0, SS_CONFIG = 1, CTRL_CENTER = 2;
    /**Lista med programmets olika knappgrupper.*/
    public static List< List <AbstractButton> > itemGroups = //multidimensionell arraylist
            new ArrayList< List< AbstractButton> >(); 
    public static boolean userIsRegular; //gästkonto standard
    
    /**
     * Konstruktören initierier knappgrupperna.
     * Anropas från <code>Main</code>-metoden vid start av program.
     */
    public MenuItemGroup() {
        for (int i = 0; i < 3; i++) {
            itemGroups.add(new ArrayList<AbstractButton>());
        }
    }
    
    /**
     * Registrerar knapp som medlem i knappgrupp.
     * De olika grupperna är: <br><br>
     * <code>SS_VIEWER</code> - knapparna aktiveras när man studerar en kortsamling<br>
     * <code>SS_CONFIG</code> - knapparna aktiveras i fönstret där man väljer 
     * inställningar inför en genomgång av kort<br>
     * <code>CTRL_CENTER</code> - knapparna aktiveras i programmets huvudfönster 
     * (programmets interna tabell)<br>
     * @param member knapp att registrera som medlem
     * @param group  knappgrupp
     */
    public static void addMember(AbstractButton member, int group) {
        
        switch (group) {
            case SS_VIEWER:
                itemGroups.get(group).add(member);
                break;
            case SS_CONFIG:
                itemGroups.get(group).add(member);
                break;
            case CTRL_CENTER:
                itemGroups.get(group).add(member);
        }
    }
    
    /**
     * Metoden inaktiverar samtliga knappgrupper.
     * Används vid varje fönsterbyte. Vid aktivering av en knappgrupp 
     * inaktiveras först samtliga knappgrupper, för att sedan aktivera
     * önskad knappgrupp.
     */
    public static void disableAllItems() {
        for (List<AbstractButton> itemGroup : itemGroups) {
            for (AbstractButton item : itemGroup) {
                item.setEnabled(false);
            }
        }
    }
    
    /**
     * Metoden aktiverar en knappgrupp.
     * Varje gång man byter till ett nytt fönster, så inaktiveras samtliga knappar
     * i samtliga grupper. Därefter aktiveras den knappgrupp som ska vara 
     * aktiverad för det fönster man befinner sig i. Som argument skickas till 
     * metoden den knappgrupp som skall aktiveras efter att samtliga knappgrupper
     * inaktiverats. 
     * @param group knappgrupp att aktivera
     */
    public static void enableGroup(final int group) {
        String actionCommand = "";
        
        disableAllItems(); //inaktivera samtliga knappar
        
        if (userIsRegular) { //vanlig användare 
            for (AbstractButton item : itemGroups.get(group)) { //aktivera grupp
                item.setEnabled(true);
            }
        } 
        
        //gäst i huvudfönster (interna tabellfönstret) ska kunna byta användare,
        //skapa en ny användare samt återställa & öppna databasen
        else if ( !userIsRegular && group == CTRL_CENTER ) { 
            for (AbstractButton item : itemGroups.get(group)) { //iterera igenom knappgrupp
                actionCommand = item.getActionCommand();
                switch (actionCommand) {
                    case "Skapa användare":      item.setEnabled(true); break;
                    case "Byt användare":        item.setEnabled(true); break;
                    case "Återställ databas":    item.setEnabled(true); break;
                    case "Administrera databas": item.setEnabled(true); break;
                    case "Öppna databas":        item.setEnabled(true); break;
                }
            }
        } 
        
        //gästanvändare som studerar kortsamling ska kunna trycka på knapp som 
        //man trycker på för att återgå till huvudmeny
        else { 
            for (AbstractButton item : itemGroups.get(group)) {
                actionCommand = item.getActionCommand();
                switch (actionCommand) {
                    case "Återgå till huvudmeny": item.setEnabled(true);
                }
            }
        }
    }
    
    /**
     * Tar bort tabellknapparna "Öppna" och "Radera" från dess knappgrupp.
     * Används när tabell i det inre fönstret uppdateras, då varje uppdatering 
     * av tabell raderar knappar från tabellfönster för att sedan skapar nya 
     * knappobjekt. De nya knappobjekten registrerar sig som nya medlemmar 
     * i knappgruppen.
     */
    public static synchronized void removeTableButtons() {
        String actionCommand = "";
        for (Iterator it = itemGroups.get(CTRL_CENTER).iterator(); it.hasNext(); ) {
            actionCommand = ((AbstractButton)it.next()).getActionCommand();
            if ((actionCommand.equals("Radera")) || (actionCommand.equals("Öppna"))) {
                it.remove();
            }
        }
    }
    
    /**
     * Talar om för klassen om användaren är gäst eller vanlig användare.
     * @param user användarobjekt
     */
    public static void setUser(User user) {
        userIsRegular = user instanceof RegularUser;
    }
}