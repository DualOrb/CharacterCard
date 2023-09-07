package charactercard.charactercard.util.database;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 */
public class Database {

    private static Connection conn = null;
    private static ErrorManager errorManager;

    public Database() {
        errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);

        //Initial Connection - Card Data
        try {
            final String url = "jdbc:sqlite:" + String.valueOf(CharacterCard.getMountsFolder()) + File.separator + "cards.db";

            conn = DriverManager.getConnection(url);

            errorManager.log("Database Connection established");

            //Setting up table
            String sql = "CREATE TABLE IF NOT EXISTS cards (\n"
                    + "player_id varchar(255),\n"
                    + "name text ,\n"
                    + "appearance text,\n"
                    + "personality text,\n"
                    + "introduction text,\n"
                    + "profession text,\n"
                    + "CONSTRAINT Pk_cards PRIMARY KEY (player_id)"
                    + ");";
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            errorManager.error("Unable to establish Database Connection: " + e);
            throw new RuntimeException(e);
        }


    }

    /**
     * Inserts a blank user entry into the table
     * @param player
     * @return
     */
    public void createBlankEntry(Player player) {
        String sql = "INSERT INTO cards(player_id,name,appearance,personality,introduction, profession) VALUES(?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,player.getUniqueId().toString());
            pstmt.setString(2,null);
            pstmt.setString(3,null);
            pstmt.setString(4,null);
            pstmt.setString(5,null);
            pstmt.setString(6,null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update a particular field in the table
     * @param player
     * @param field
     */
    public void updateCard(Player player, String field, String text) {
        String sql = "UPDATE cards SET " + field + " = ? "
                + "WHERE player_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,text);
            pstmt.setString(2,player.getUniqueId().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve a player's card
     * @param player
     * @return
     */
    public Card getCard(Player player) {

        String sql = "SELECT * FROM cards WHERE player_id = ?";

        ResultSet rs;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,player.getUniqueId().toString());
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArrayList<Object> o = new ArrayList<>();
                o.add(rs.getString(1));
                o.add(rs.getString(2));
                o.add(rs.getString(3));
                o.add(rs.getString(4));
                o.add(rs.getString(5));
                o.add(rs.getString(6));


                return new Card(o);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
