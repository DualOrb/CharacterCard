package charactercard.charactercard.card.handlers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.managers.RPManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerNameHandler implements Listener {
    public PlayerNameHandler(CharacterCard plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Toggles visibility of name when player crouches/uncrouches
     */
    @EventHandler
    public void onPlayerCrouchToggle(PlayerToggleSneakEvent event) {
        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
        try {
            RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);

            if(!rpManager.isRPEnabled(event.getPlayer())) return;

            rpManager.updateNameVisibility(event.getPlayer(), event.getPlayer().isSneaking());
        } catch (Throwable e) {
            errorManager.error("Failure on player crouch",event.getPlayer(),e);
        }


    }

    /**
     * Removes armor stand when player leaves
     * @param event
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
        try {
            RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);

            if (!rpManager.isRPEnabled(event.getPlayer())) return;

            rpManager.removeRpPlayer(event.getPlayer());
        } catch (Throwable e) {
            errorManager.error("Failure on player disconnect",event.getPlayer(),e);
        }
    }
}

