package charactercard.charactercard.card.commands;

import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.managers.NoteTypes;
import charactercard.charactercard.util.managers.RPManager;
import charactercard.charactercard.util.managers.SoundBuilder;
import charactercard.charactercard.util.services.ServiceLocator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static charactercard.charactercard.util.managers.NoteTypes.QUARTER;
import static charactercard.charactercard.util.managers.NoteTypes.QUARTER_REST;

public class RPToggle implements CommandExecutor {

    private final String COLOR = "#357cf0";
    private final String CCARD = ChatColor.GRAY + "--------------------[" + ChatColor.of(COLOR) + "RP MODE" + ChatColor.GRAY + "]--------------------";


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (!(sender.hasPermission("CharacterCard.rp-toggle"))) {
            return false;
        }

        Player player = (Player)sender;
        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);

        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
        try {

            if (!rpManager.getRpPlayers().contains(player.getUniqueId())) {
                rpManager.addRpPlayer(player);
                player.sendMessage("");
                player.sendMessage(CCARD);
                player.sendMessage(ChatColor.of(COLOR) + "[RIGHT-CLICK]" + ChatColor.GRAY + "            Shows the player's introduction");
                //player.sendMessage(ChatColor.of(COLOR) + "[SHIFT-RIGHT-CLICK]" + ChatColor.GRAY + "   Opens the player's Character Card");
                player.sendMessage("");

                player.sendMessage(ChatColor.of(COLOR) + "Type W or S to perform whisper or shout" + ChatColor.GRAY + "(3 50)");
                player.sendMessage("");

                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.5f);
                //            SoundBuilder song = new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BANJO);
                //            song.addNote(Note.Tone.C.ordinal(), QUARTER).addNote(Note.Tone.D.ordinal(),QUARTER).addNote(Note.Tone.C.ordinal(),QUARTER_REST).addNote(Note.Tone.A.ordinal(),QUARTER);
                //            song.play(player);
            } else {
                rpManager.removeRpPlayer(player);
                player.sendMessage(ChatColor.of(COLOR) + "RP Toggled Off");

                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.1f);
            }
        } catch (Throwable e) {
            errorManager.error("Failure on RP Toggle Event",player,e);
        }

        return true;

    }
}
