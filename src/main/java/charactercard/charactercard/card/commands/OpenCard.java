package charactercard.charactercard.card.commands;

import charactercard.charactercard.card.guipages.CardPage;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getPlayer;

public class OpenCard implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {return false;}
        if(!(sender.hasPermission("CharacterCard.OpenCard.self"))) {return false;}

        Player player = (Player) sender;

        try {
            player.playSound(player.getLocation(), Sound.BLOCK_BARREL_CLOSE, 2.5F,2.5F);
            if(args.length != 0) {
                if(!sender.hasPermission("CharacterCard.OpenCard.others")) {sender.sendMessage(ChatColor.RED + "No permission to view other player's cards");}
                if(!(getPlayer(args[0]) instanceof Player) || getPlayer(args[0]) == null) {sender.sendMessage(ChatColor.RED + "Please select a valid player.");}

                Player player2 = Bukkit.getPlayer(args[0]);

                new CardPage(player2,player);  //Will need to change to sending player vs target
                return true;

            } else {
                new CardPage(player,player);
                return true;
            }

        } catch (Throwable e) {
            ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
            errorManager.error("Failed to Open Character Card", player,e);
            return false;
        }
    }
}
