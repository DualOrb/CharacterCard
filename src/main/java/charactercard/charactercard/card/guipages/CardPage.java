package charactercard.charactercard.card.guipages;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.database.Card;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.gui.GUIBuilder;
import charactercard.charactercard.util.gui.ItemManager;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class CardPage {

    public CardPage(Player target, Player sender) {
        sender.playSound(sender.getLocation(), Sound.BLOCK_BARREL_OPEN, 0.5f, 0.5f);

        Database database = ServiceLocator.getLocator().getService(Database.class);
        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
        GUIBuilder guiBuilder = ServiceLocator.getLocator().getService(GUIBuilder.class);

        Card card = database.getCard(target);
        if(card == null) {
            errorManager.error(target.getDisplayName() + " does not have a character card!");
            return;
        }


        ItemManager itemManager = ServiceLocator.getLocator().getService(ItemManager.class);

        Inventory GUI;
        if(card.getName() == null) {
            GUI = Bukkit.createInventory(target, 9, ChatColor.DARK_PURPLE + target.getDisplayName());
        } else {
            GUI = Bukkit.createInventory(target, 9, ChatColor.DARK_PURPLE + card.getName());
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullmeta = (SkullMeta)head.getItemMeta();
        if(card.getName() == null) {
            skullmeta.setDisplayName(ChatColor.GOLD + "Username: " + ChatColor.GRAY + target.getName());
        } else {
            skullmeta.setDisplayName(ChatColor.DARK_PURPLE + "RP Name: " + ChatColor.GRAY + card.getName());
            ArrayList<String> headlore = new ArrayList<>();
            headlore.add("| " + ChatColor.GRAY + target.getName());
            if(target.getUniqueId().equals(sender.getUniqueId())) {
                headlore.add("--------");
                headlore.add(ChatColor.BLUE + "Click To Change");
            }
            skullmeta.setLore(headlore);
        }



        NamespacedKey key = new NamespacedKey(CharacterCard.getPlugin(),target.getUniqueId().toString());
        skullmeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "card");


        skullmeta.setOwningPlayer(target);
        head.setItemMeta(skullmeta);
        GUI.setItem(0,head);
        itemManager.addPermItem(head);

        //Leather Tunic - Appearance
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = chestplate.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Appearance");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------");

        if(card.getAppearance() != null) {
            lore.addAll(guiBuilder.formatLore(card.getAppearance()));
        } else {
            lore.addAll(guiBuilder.formatLore(
                    "Covered in food scraps. A hungry garbage muncher. Visible fleas and a smell so putrid you can see it"
            ));
        }
        if(target.getUniqueId().equals(sender.getUniqueId())) {
            lore.add(ChatColor.DARK_GRAY + "------------");
            lore.add(ChatColor.BLUE + "Click To Change");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        chestplate.setItemMeta(meta);
        GUI.setItem(4,chestplate);
        itemManager.addPermItem(chestplate);

        //Redstone Dust - Personality / Alignment
        ItemStack personality = new ItemStack(Material.REDSTONE);
        meta = personality.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Personality / Alignment");
        lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------");

        if(card.getPersonality() != null) {
            lore.addAll(guiBuilder.formatLore(card.getPersonality()));
        } else {
            lore.addAll(guiBuilder.formatLore(
                    "He seethes with vengeance towards his overarching rulers. Always on edge, ready to pounce on the nearest entity he deems an enemy."
            ));
        }
        if(target.getUniqueId().equals(sender.getUniqueId())) {
            lore.add(ChatColor.DARK_GRAY + "------------");
            lore.add(ChatColor.BLUE + "Click To Change");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        personality.setItemMeta(meta);
        GUI.setItem(6,personality);
        itemManager.addPermItem(personality);

        //Paper - Instroductions
        ItemStack introduction = new ItemStack(Material.PAPER);
        meta = introduction.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "Introductions");
        lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------");

        if(card.getIntroduction() != null) {
            lore.addAll(guiBuilder.formatLore(card.getIntroduction()));
        } else {
            lore.addAll(guiBuilder.formatLore(
                    "Why hello there! I am but a humble serf, going about my day."
            ));
        }
        if(target.getUniqueId().equals(sender.getUniqueId())) {
            lore.add(ChatColor.DARK_GRAY + "------------");
            lore.add(ChatColor.BLUE + "Click To Change");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        introduction.setItemMeta(meta);
        GUI.setItem(8,introduction);
        itemManager.addPermItem(introduction);

        //Emerald - Profession
        ItemStack profession = new ItemStack(Material.EMERALD);
        meta = profession.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Profession");
        lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------");

        if(card.getIntroduction() != null) {
            lore.addAll(guiBuilder.formatLore(card.getProfession()));
        } else {
            lore.addAll(guiBuilder.formatLore(
                    "Sand Farmer"
            ));
        }
        if(target.getUniqueId().equals(sender.getUniqueId())) {
            lore.add(ChatColor.DARK_GRAY + "------------");
            lore.add(ChatColor.BLUE + "Click To Change");
        }
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        profession.setItemMeta(meta);
        GUI.setItem(2,profession);
        itemManager.addPermItem(profession);


        GUI = guiBuilder.fillWithBackgroundItem(GUI, Material.BLACK_STAINED_GLASS_PANE);

        sender.openInventory(GUI);
    }
}
