package charactercard.charactercard.card.handlers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.card.guipages.CardPage;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.managers.RPManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatHandler implements Listener {

    public static HashMap<UUID, String> textQueue;

    public ChatHandler(CharacterCard plugin) {
        textQueue = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Responsible for creating a character card for each player that joins the server
     * @param event
     */
    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        if(!textQueue.containsKey(event.getPlayer().getUniqueId())) return;

        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);

        event.setCancelled(true);

        try {
            if(textQueue.get(event.getPlayer().getUniqueId()).equals("name")) {
                if(event.getMessage().length() > 20) {
                    errorManager.error("Name Length too long. Try Again",event.getPlayer());
                    event.setCancelled(true);
                    return;
                }
            }

            //Process text output
            Database database = ServiceLocator.getLocator().getService(Database.class);

            database.updateCard(event.getPlayer(), textQueue.get(event.getPlayer().getUniqueId()),event.getMessage());

            if(textQueue.get(event.getPlayer().getUniqueId()).equals("name")) {
                Bukkit.getScheduler().runTaskLater(CharacterCard.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);

                        rpManager.removeRpPlayer(event.getPlayer());
                        rpManager.addRpPlayer(event.getPlayer());
                    }
                },1L);

            }



            //Just did this to get rid of the asynchronous part of the player chat
            Bukkit.getScheduler().runTaskLater(CharacterCard.getPlugin(), new Runnable() {
                @Override
                public void run() {

                    new CardPage(event.getPlayer(),event.getPlayer());
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME,0.5f,0.5f);
                    textQueue.remove(event.getPlayer().getUniqueId());
                }
            },1L);
        } catch (Throwable e) {
            errorManager.error("Failed to handle Chat Command",event.getPlayer(),e);
        }


    }

    public static void queueTextInput(String field, UUID id) {
        Player player = Bukkit.getPlayer(id);
        player.sendMessage(ChatColor.GREEN + "Type your input below");

        textQueue.put(id,field);
    }

}
