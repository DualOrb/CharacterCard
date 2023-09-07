package charactercard.charactercard.card.handlers;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.database.Card;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.managers.ErrorManager;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerJoinHandler implements Listener {

    private static List<String> names;
    private static List<String> appearances;
    private static List<String> personalities;
    private static List<String> introductions;
    private static List<String> professions;
    public PlayerJoinHandler(CharacterCard plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialiseLists();
    }

    /**
     * Responsible for creating a character card for each player that joins the server
     * @param event
     */
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if(event.getPlayer() == null) return;
        Database database = ServiceLocator.getLocator().getService(Database.class);
        Card card = database.getCard(event.getPlayer());
        if(card != null) return;

        ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);

        try {
            database.createBlankEntry(event.getPlayer());

            //Populate the new character entry

            //Makes a random character based on the text files provided
            Random rand = new Random();
            int r = rand.nextInt(names.size()-1);
            database.updateCard(event.getPlayer(), "name",names.get(r));

            rand = new Random();
            r = rand.nextInt(appearances.size()-1);
            database.updateCard(event.getPlayer(), "appearance",appearances.get(r));

            rand = new Random();
            r = rand.nextInt(personalities.size()-1);
            database.updateCard(event.getPlayer(), "personality",personalities.get(r));

            rand = new Random();
            r = rand.nextInt(introductions.size()-1);
            database.updateCard(event.getPlayer(), "introduction",introductions.get(r));

            rand = new Random();
            r = rand.nextInt(professions.size()-1);
            database.updateCard(event.getPlayer(), "profession",professions.get(r));
        } catch(Throwable e) {
            errorManager.error("Failed to process Player Join - Did not generate new Character Card",event.getPlayer(),e);
        }
    }

    /**
     * Reads all the lists from a file
     */
    private void initialiseLists() {
        names = new ArrayList<>();
        appearances = new ArrayList<>();
        personalities = new ArrayList<>();
        introductions = new ArrayList<>();
        professions = new ArrayList<>();

        //Names
        try (Stream<String> stream = Files.lines(Paths.get(CharacterCard.getPluginFolder().toString() + File.separator + "names.txt"))) {

            names = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //appearances
        try (Stream<String> stream = Files.lines(Paths.get(CharacterCard.getPluginFolder().toString() + File.separator + "appearances.txt"))) {

            appearances = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //personalities
        try (Stream<String> stream = Files.lines(Paths.get(CharacterCard.getPluginFolder().toString() + File.separator + "alignments.txt"))) {
            personalities = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //introductions
        try (Stream<String> stream = Files.lines(Paths.get(CharacterCard.getPluginFolder().toString() + File.separator + "introductions.txt"))) {
            introductions = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Professions
        try (Stream<String> stream = Files.lines(Paths.get(CharacterCard.getPluginFolder().toString() + File.separator + "professions.txt"))) {
            professions = stream
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
