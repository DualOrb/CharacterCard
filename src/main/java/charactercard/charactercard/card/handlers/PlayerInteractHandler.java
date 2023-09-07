package charactercard.charactercard.card.handlers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.database.Card;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.managers.RPManager;
import charactercard.charactercard.util.services.ServiceLocator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerInteractHandler implements Listener {

    private static ArrayList<UUID> canResend;

    public PlayerInteractHandler(CharacterCard plugin) {
        canResend = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Responsible for creating a character card for each player that joins the server
     * @param event
     */
    @EventHandler
    public void onPlayerInteractPlayer(PlayerInteractAtEntityEvent event) {
        if(!(event.getRightClicked() instanceof Player)) return;

        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
        Player clickedPlayer = (Player)event.getRightClicked();

        if(!rpManager.isRPEnabled(event.getPlayer())) return;
        if(!rpManager.isRPEnabled(clickedPlayer)) return;

        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);

        try {
            Database database = ServiceLocator.getLocator().getService(Database.class);
            Card card = database.getCard(clickedPlayer);

            if(card == null) return; //Player is probably clicking an npc

            if(CharacterCard.getMountConfig().getBoolean("basic.right-click-intro") && !canResend.contains(event.getPlayer().getUniqueId())) {

                //Message formulating using text components for hoverables
                TextComponent name = new TextComponent(net.md_5.bungee.api.ChatColor.DARK_PURPLE + card.getName());

                name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new Text(net.md_5.bungee.api.ChatColor.BLUE + Bukkit.getPlayer(card.getPlayerId()).getName() + "\n" + ChatColor.of("#fcba03") +  "Click to open card")));
                name.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ccard " + Bukkit.getPlayer(card.getPlayerId()).getName()));

                TextComponent main = new TextComponent("");
                main.addExtra(name);
                main.addExtra(" : " + net.md_5.bungee.api.ChatColor.GRAY + card.getIntroduction());

                event.getPlayer().spigot().sendMessage(main);
                canResend.add(event.getPlayer().getUniqueId());
            }


            //Makes the event not trigger twice
            Bukkit.getScheduler().runTaskLater(CharacterCard.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    canResend.remove(event.getPlayer().getUniqueId());
                }
            },30L);
        } catch (Throwable e) {
            errorManager.error("Failed to handle Interact Event", event.getPlayer(),e);
        }
    }
}
