package charactercard.charactercard.util.gui;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.card.handlers.ChatHandler;
import charactercard.charactercard.util.database.Card;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static org.bukkit.Material.*;

public class GUIHandler implements Listener {

    private ItemManager itemManager;

    public GUIHandler(CharacterCard plugin) {
        Bukkit.getPluginManager().registerEvents(this,plugin);
        itemManager = ServiceLocator.getLocator().getService(ItemManager.class);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        //Perm items can't be dropped in survival
        if(itemManager.getPermItems().contains(event.getItemDrop().getItemStack())){
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if(itemManager.getPermItems().contains(event.getCurrentItem())) event.setCancelled(true);
        if(event.getClickedInventory() == null) return; //Catch if player just presses esc to exit inventory
        if(event.getClickedInventory().getItem(event.getSlot()) == null) return;
        if(event.getClickedInventory().getItem(0) == null) return;

        int clicked = event.getSlot();
        Player player = (Player)event.getWhoClicked();

        if(!event.getClickedInventory().getItem(0).getType().equals(Material.PLAYER_HEAD)) return;
        ItemStack head1 = event.getClickedInventory().getItem(0);
        SkullMeta skullmeta1 = (SkullMeta)head1.getItemMeta();
        OfflinePlayer offlinePlayer = skullmeta1.getOwningPlayer();

        NamespacedKey key = new NamespacedKey(CharacterCard.getPlugin(),player.getUniqueId().toString());
        if(!skullmeta1.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {  //Needs to have persistent data type set
            return;
        }

        Material clickedMaterial = event.getClickedInventory().getItem(event.getSlot()).getType();

        player.closeInventory();
        if(clickedMaterial.equals(LEATHER_CHESTPLATE)) {
            ChatHandler.queueTextInput("appearance",player.getUniqueId());
        } else if(clickedMaterial.equals(REDSTONE)) {
            ChatHandler.queueTextInput("personality",player.getUniqueId());
        } else if(clickedMaterial.equals(PAPER)) {
            ChatHandler.queueTextInput("introduction",player.getUniqueId());
        } else if(clickedMaterial.equals(PLAYER_HEAD)) {
            ChatHandler.queueTextInput("name",player.getUniqueId());
        } else if(clickedMaterial.equals(EMERALD)) {
            ChatHandler.queueTextInput("profession",player.getUniqueId());
        }

        event.setCancelled(true);
    }
}
