package charactercard.charactercard.util.managers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.card.NameTag;
import charactercard.charactercard.util.database.Card;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.services.ServiceLocator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class RPManager {

    private ArrayList<UUID> rpPlayers;
    private ArrayList<NameTag> tags;
    private final String PREFIX = net.md_5.bungee.api.ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.DARK_PURPLE + "RP" + ChatColor.GRAY + "] ";

    public RPManager() {
        rpPlayers = new ArrayList<>();
        tags = new ArrayList<>();
    }

    /**
     * Adds a player as an "RP" player
     * - Updates name above head
     * - Can see RP Chats
     * - Clicks to open character cards
     * @param player
     */
    public void addRpPlayer(Player player) {
        rpPlayers.add(player.getUniqueId());

        Database database = ServiceLocator.getLocator().getService(Database.class);
        Card card = database.getCard(player);

        if(CharacterCard.getMountConfig().getBoolean("basic.show-rp-name")) {
            if(CharacterCard.getMountConfig().getBoolean("names.show-rp-prefix")) {
                tags.add(new NameTag(player,PREFIX,card.getName(), "#" + CharacterCard.getMountConfig().getString("names.name-color")));
            } else {
                tags.add(new NameTag(player,"",card.getName(), "#" + CharacterCard.getMountConfig().getString("names.name-color")));
            }
        }


    }

    /**
     * Removes a player as an RP player
     * @param player
     */
    public void removeRpPlayer(Player player) {
        rpPlayers.remove(player.getUniqueId());

        NameTag tag = getTag(player);
        if(tag == null) return;
        tag.delete();
        tags.remove(tag);
    }

    /**
     * Gets list of all the current RP Players
     * @return
     */
    public ArrayList<UUID> getRpPlayers() {
        return rpPlayers;
    }

    /**
     * Checks if RP is enabled for a player
     * @param player
     * @return
     */
    public boolean isRPEnabled(Player player) {
        return rpPlayers.contains(player.getUniqueId());
    }

    /**
     * Enable or disable the player's custom RP name
     * @param player
     * @param sneaking
     */
    public void updateNameVisibility(Player player, boolean sneaking) {
        getTag(player).updateNameVisibility(sneaking);
    }

    /**
     * Gets a player's RP Name Tag
     * @param player
     * @return
     */
    public NameTag getTag(Player player) {
        for(NameTag tag: tags) {
            if(tag.getPlayerId().equals(player.getUniqueId())) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Updates the RP Name of a player
     * @param player
     */
    public void updateName(Player player) {
        if(!isRPEnabled(player)) return;

        Database database = ServiceLocator.getLocator().getService(Database.class);

        getTag(player).setName(database.getCard(player).getName());
    }
}
