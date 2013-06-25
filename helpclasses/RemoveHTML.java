/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helpclasses;

import cards.Card;
import cards.CardCollection;
import database.SQL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kristian Mörling
 */
public class RemoveHTML {
    private String[] tags = {  "&lt", "&gt", "<br>", "&nbsp;&nbsp;&nbsp;" };
    private String[] ctrlCharacters = { ">", "<", "\n", "\t" };
    private String dbPath;
    
    public RemoveHTML() throws SQLException {
//        dbPath = this.getClass().getResource("/database/study_it").toString();
        dbPath = "file:/home/kristian/Downloads/studyit_updated_db/database/study_it";
        SQL.connect(dbPath);
    }
    
    public void removeHTML() throws SQLException {
        List <CardCollection> cc = new ArrayList<CardCollection>();
        cc = getTablesAsCollections();
        
        Card card;
        String frontSide, backSide;
        List <Card> cards = new ArrayList<Card>();
        for (int i = 0; i < cc.size(); i++) {
            
            cards = cc.get(i).getCards();
            System.out.println("\nUppdaterar " + cc.get(i).getCollectionName());
            for (int j = 0; j < cards.size(); j++) {
                //hämta kort
                card = cards.get(j);
                
                //hämta framside- och baksidetext
                frontSide = card.getFrontSide();
                backSide = card.getBackSide();
                
                //ta bort HTML
                frontSide = frontSide.replaceAll(tags[0], ctrlCharacters[0]);
                frontSide = frontSide.replaceAll(tags[1], ctrlCharacters[1]);
                frontSide = frontSide.replaceAll(tags[2], ctrlCharacters[2]);
                backSide = backSide.replaceAll(tags[0], ctrlCharacters[0]);
                backSide = backSide.replaceAll(tags[1], ctrlCharacters[1]);
                backSide = backSide.replaceAll(tags[2], ctrlCharacters[2]);
                
                //spara ändringar till kortobjekt 
                card.setFrontSide(frontSide);
                card.setBackSide(backSide);
            }
            
            //spara kortsamling till databas
            CollectionManager.updateCollection(cc.get(i));
            System.out.println(cc.get(i).getCollectionName() + " uppdaterad.");
        }
        
        SQL.disconnect();
    }
    
    public static ArrayList<CardCollection>
            getTablesAsCollections() throws SQLException {
        ResultSet rs;
        String query = "SELECT name from sqlite_master "
                    + "where type in ('table','view') "
                    + "AND name like '%1___'";

/*        String query = "SELECT name from sqlite_master "
                    + "where type in ('table','view') "
                    + "AND name like '%kap10_1002'";
*/                    
        //Skapa 2 listor, i den ena lista sparas namnen över tabeller att
        //skapa CardCollection objekt utav. I den andra listan sparas
        //CardCollection objekten.
        ArrayList<String> tableNames = new ArrayList<String>();
        ArrayList<CardCollection> cc = new ArrayList<CardCollection>();
        
        //spara namn över tabeller att skapa CardCollection objekt utav
        rs = SQL.resultSet(query);
        while(rs.next()) {
            tableNames.add(rs.getString(1));
        }
        rs.close();

        //spara tabeller som CardCollection objekt i lista
        for (Iterator it = tableNames.iterator(); it.hasNext(); ) {
            cc.add(getTableAsCollection( it.next().toString() ) );
        }
        return cc; //returnera lista innehållande CardCollection-objekt
    }
    
    private static CardCollection getTableAsCollection(String tableName) throws SQLException {
        CardCollection cardCollection; //lista att spara varje kort i
        ArrayList<Card> cards = new ArrayList<Card>();
        ResultSet rs = SQL.resultSet( "SELECT * FROM " + tableName + " ORDER BY CardId ASC" );
        
        //spara varje rad från tabell som ett kort i en lista
        while (rs.next()) {
            cards.add(new Card(rs.getInt(1), rs.getLong(2), rs.getString(3),
                    rs.getString(4), rs.getInt(5)));
        }
        rs.close();
        //lista med kort blir ett objekt av typen CardCollection
        cardCollection = new CardCollection(cards, tableName);
        return cardCollection;
    }
}
