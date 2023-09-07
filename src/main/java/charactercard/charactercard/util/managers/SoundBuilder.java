package charactercard.charactercard.util.managers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.card.guipages.CardPage;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A common interface to build songs / sounds from
 * @author Nicksarmour
 */
public class SoundBuilder {

    private int counter;
    private ArrayList<Pair<Note.Tone,NoteTypes>> song;
    private Sound sound;

    /**
     * Creates a new sound / song with a specific sound
     * @param sound
     */
    public SoundBuilder(Sound sound) {
        this.sound = sound;
        counter = 0;
        song = new ArrayList<>();
    }

    /**
     * Adds a note to the song in builder notation
     * @param note
     * @param type
     * @return
     */
    public SoundBuilder addNote(int note, NoteTypes type) {
        song.add(new Pair(note,type));
        return this;
    }

    /**
     * Plays the song currently in the songbuilder
     * @param player
     */
    public void play(Player player) {

        for(Pair p: song) {
            Note.Tone tone = Note.Tone.values()[(int)p.getFirst()];
            Bukkit.getScheduler().runTaskLater(CharacterCard.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if(((NoteTypes)p.getSecond()).equals(NoteTypes.QUARTER_REST)) return;

                    player.playSound(player.getLocation(), sound, 1f,(float)(0.5*(2^((int)p.getFirst())/12)));
                }
            },counter * 20);
            counter += ((NoteTypes)p.getSecond()).getValue();
        }
    }
}
