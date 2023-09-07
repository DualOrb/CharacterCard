package charactercard.charactercard;

import charactercard.charactercard.card.commands.OpenCard;
import charactercard.charactercard.card.commands.RPToggle;
import charactercard.charactercard.card.handlers.*;
import charactercard.charactercard.util.database.Database;
import charactercard.charactercard.util.gui.GUIBuilder;
import charactercard.charactercard.util.gui.GUIHandler;
import charactercard.charactercard.util.gui.ItemManager;
import charactercard.charactercard.util.managers.*;
import charactercard.charactercard.util.services.ServiceLocator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CharacterCard extends JavaPlugin {

    private static Plugin plugin;   //This plugin
    private static Path pluginFolder; //Root file path
    private static Path mountsFolder; //file path for mounts

    private static File mountConfigFile;
    private static FileConfiguration mountConfig;

    private static ServiceLocator serviceLocator;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        pluginFolder = Path.of(getDataFolder().getPath());
        try {
            Files.createDirectories(Path.of(pluginFolder + "/" + "playerData"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mountsFolder = Path.of(String.valueOf(pluginFolder),"playerData");

        //Load conf file
        createMountConfig();

        //Register services
        serviceLocator = ServiceLocator.getLocator();
        serviceLocator.registerService(ErrorManager.class, new ErrorManager());
        serviceLocator.registerService(Database.class, new Database());

        serviceLocator.registerService(ItemManager.class, new ItemManager());
        serviceLocator.registerService(GUIBuilder.class, new GUIBuilder());

        try {
            serviceLocator.registerService(RPManager.class, new RPManager());
        } catch(Throwable e) {
            ErrorManager errorManager = ServiceLocator.getLocator().getService(ErrorManager.class);
            errorManager.error("Failed to initialise RPManager",e);
        }


        saveResource("names.txt",false);
        saveResource("appearances.txt",false);
        saveResource("alignments.txt",false);
        saveResource("introductions.txt",false);
        saveResource("professions.txt",false);

        //Check Dependencies
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {Bukkit.getLogger().info("[Simple-Mounts] " + "Server is missing hard dependency: PlaceholderAPI");}

        log("Successfully loaded all dependencies");

        log("Initialising data...");

        setup();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        //com.lkeehl.tagapi.TagAPI.onDisable();

        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);

        synchronized(Bukkit.getOnlinePlayers()) {
            rpManager.getRpPlayers().stream().forEach(rp -> rpManager.removeRpPlayer(Bukkit.getPlayer(rp)));
        }
    }

    private void setup() {
        log("Items and Recipes Loaded");

        //Load Handlers

        new GUIHandler(this);
        new PlayerJoinHandler(this);
        new ChatHandler(this);
        new PlayerInteractHandler(this);
        new RPChatHandler(this);
        new PlayerNameHandler(this);


        log("Handlers Loaded");

        //Register Commands
        this.getCommand("ccard").setExecutor(new OpenCard());
        this.getCommand("rp-toggle").setExecutor(new RPToggle());

        log("Commands Loaded");

        log("GUIs Loaded");
    }

    private void log(String log) {
        Bukkit.getLogger().info("[Character Cards] " + log);
    }

    //If config file is not there, create a config file
    public void createMountConfig() {

        mountConfigFile = new File(String.valueOf(pluginFolder), "config.yml");
        if(!mountConfigFile.exists()) {
            mountConfigFile.getParentFile().mkdirs();
            saveResource("config.yml",false);
        }

        mountConfig = new YamlConfiguration();
        try {
            mountConfig.load(mountConfigFile);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void reloadCustomConfig() {

        mountConfigFile = new File(String.valueOf(pluginFolder), "config.yml");
        mountConfig = new YamlConfiguration();
        try {
            mountConfig.load(mountConfigFile);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getMountConfig() {
        return mountConfig;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Path getMountsFolder() {
        return mountsFolder;
    }

    public static Path getPluginFolder() {return pluginFolder; }

}