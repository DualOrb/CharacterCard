package charactercard.charactercard.util.database;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.UUID;

public class Card {

    private final UUID playerId;

    private final String name;
    private final String appearance;
    private final String personality;
    private final String introduction;
    private final String profession;

    /**
     * A class for holding database rows retrieved of Character Cards
     * @param row
     */
    public Card(ArrayList<Object> row) {
        playerId = UUID.fromString((String)row.get(0));

        if(row.get(1) == null) {
            name = Bukkit.getPlayer(playerId).getName();
        } else {
            name = (String)row.get(1);
        }

        if(row.get(2) == null) {
            appearance = "Covered in food scraps. A hungry garbage muncher. Visible fleas and a smell so putrid you can see it";
        } else {
            appearance = (String)row.get(2);
        }

        if(row.get(3) == null) {
            personality = "He seethes with vengeance towards his overarching rulers. Always on edge, ready to pounce on the nearest entity he deems an enemy.";
        } else {
            personality = (String)row.get(3);
        }

        if(row.get(4) == null) {
            introduction = "Why hello there! I am but a humble serf, going about my day.";
        } else {
            introduction = (String)row.get(4);
        }

        if (row.get(5) == null) {
            profession = "Unemployed";
        } else {
            profession = (String)row.get(5);
        }


    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public String getAppearance() {
        return appearance;
    }

    public String getPersonality() {
        return personality;
    }

    public String getIntroduction() {
        return introduction;
    }
    public String getProfession() {return profession;}
}
