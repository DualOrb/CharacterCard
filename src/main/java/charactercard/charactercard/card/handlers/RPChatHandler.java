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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.EventPriority.LOWEST;

public class RPChatHandler implements Listener {

    private int NORMAL_RANGE;
    private int SHOUT_RANGE;
    private int WHISPER_RANGE;

    private final String PREFIX = ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "RP" + ChatColor.GRAY + "]";

    public RPChatHandler(CharacterCard plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        NORMAL_RANGE = CharacterCard.getMountConfig().getInt("range.normal-range");
        SHOUT_RANGE = CharacterCard.getMountConfig().getInt("range.shout-range");
        WHISPER_RANGE = CharacterCard.getMountConfig().getInt("range.whisper-range");

    }

    /**
     * Parses the msg and overrides the custom chat system
     * @param event
     */
    @EventHandler(priority = HIGHEST)
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
        try {
            if (!rpManager.isRPEnabled(player)) return;
            if (ChatHandler.textQueue.containsKey(event.getPlayer().getUniqueId())) return;

            //Begin RP Overriding
            event.setCancelled(true);

            //Async Chat Event must be run on main server thread, not helper
            Bukkit.getScheduler().runTaskLater(CharacterCard.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    //Bukkit.getLogger().info("Count: " + player.getNearbyEntities(10,10,10).stream().filter(en->en instanceof Player).count());

                    //Determine if its a normal, whisper, or shout
                    String[] splitString = event.getMessage().split(" ");

                    List<Entity> nearbyEntities;
                    String msg = event.getMessage();
                    if (splitString[0].equalsIgnoreCase("w")) {
                        msg = msg.substring(1);
                        msg = ChatColor.ITALIC + "Whispers" + msg;
                        nearbyEntities = player.getNearbyEntities(WHISPER_RANGE, WHISPER_RANGE, WHISPER_RANGE).stream().filter(en -> en instanceof Player).collect(Collectors.toList());
                    } else if (splitString[0].equalsIgnoreCase("s")) {
                        msg = msg.substring(1);
                        msg = ChatColor.BOLD + "Shouts" + msg;
                        nearbyEntities = player.getNearbyEntities(SHOUT_RANGE, SHOUT_RANGE, SHOUT_RANGE).stream().filter(en -> en instanceof Player).collect(Collectors.toList());
                    } else {
                        nearbyEntities = player.getNearbyEntities(NORMAL_RANGE, NORMAL_RANGE, NORMAL_RANGE).stream().filter(en -> en instanceof Player).collect(Collectors.toList());
                    }
                    Database database = ServiceLocator.getLocator().getService(Database.class);
                    Card card = database.getCard(player);

                    //Message formulating using text components for hoverables
                    TextComponent name = new TextComponent(ChatColor.DARK_PURPLE + card.getName());

                    name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(net.md_5.bungee.api.ChatColor.BLUE + Bukkit.getPlayer(card.getPlayerId()).getName() + "\n" + ChatColor.of("#fcba03") +  "Click to open card")));
                    name.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ccard " + Bukkit.getPlayer(card.getPlayerId()).getName()));

                    TextComponent main = new TextComponent(PREFIX + " ");
                    main.addExtra(name);
                    main.addExtra(" : " + ChatColor.GRAY + msg);

                    nearbyEntities.stream()
                            .filter(en -> rpManager.isRPEnabled((Player) en))
                            .forEach(en -> en.spigot().sendMessage(main));

                    player.spigot().sendMessage(main);
                }
            }, 3L);
        } catch (Throwable e) {
            errorManager.error("Failure on RP Chat Handler",player,e);
        }
    }
}
