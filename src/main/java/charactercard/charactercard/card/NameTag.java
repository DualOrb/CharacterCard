package charactercard.charactercard.card;

import charactercard.charactercard.CharacterCard;
import charactercard.charactercard.util.managers.RPManager;
import charactercard.charactercard.util.services.ServiceLocator;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityTurtle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A class for implementing custom entity name tags on the server
 */
public class NameTag implements Listener {

    private UUID playerId;
    private ArrayList<UUID> entities;
    private String name;


    public NameTag(Player player, String prefix, String name, String color) {
        entities = new ArrayList<>();
        playerId = player.getUniqueId();
        this.name = name;

        addNameAbove(player,prefix, name,color);

        Bukkit.getPluginManager().registerEvents(this, CharacterCard.getPlugin());
    }

    private void addNameAbove(Player player, String prefix, String name, String color) {

        int HEIGHT = CharacterCard.getMountConfig().getInt("basic.height");

        Turtle lastTurtle = null;
        for(int i = 0; i < HEIGHT; i++) {
            Turtle turtle = (Turtle) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.TURTLE);
            entities.add(turtle.getUniqueId());
            turtle.setInvisible(true);
            turtle.setInvulnerable(true);
            turtle.setSilent(true);
            turtle.setAI(false);
            turtle.setBaby();
            turtle.setAge(-2000000000);
            if(lastTurtle == null) {
                player.addPassenger(turtle);
            } else {
                lastTurtle.addPassenger(turtle);
            }

            lastTurtle = turtle;
        }

            ArmorStand armorStand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
            entities.add(armorStand.getUniqueId());
            armorStand.setVisible(false);
            armorStand.setInvulnerable(true);
            armorStand.setSmall(true);
            armorStand.setAI(false);

            armorStand.setMarker(true);

            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "RP" +
                    ChatColor.GRAY + "] " +
                    ChatColor.of(color) + "" + ChatColor.BOLD + name);

            lastTurtle.addPassenger(armorStand);

            //Bukkit.getLogger().info("IDs: " + turlte1Id + "|" + turlte2Id + "|" + armorStandId);

    }

    /**
     * Removes the tag from the world (only to be used once)
     */
    public void delete() {
        //Bukkit.getLogger().info("IDs: " + turlte1Id + "|" + turlte2Id + "|" + armorStandId);
        for(UUID uuid: entities) {
            Bukkit.getEntity(uuid).remove();
        }

        PlayerTeleportEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        EntityDismountEvent.getHandlerList().unregister(this);
        EntitiesUnloadEvent.getHandlerList().unregister(this);
    }

    /**
     * Updates the name for a given tag
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        entities.stream().filter(uuid -> Bukkit.getEntity(uuid) instanceof ArmorStand)
                        .forEach(uuid -> Bukkit.getEntity(uuid).setCustomName(name));
    }



    /**
     * Updates the visibility of the custom name tag
     * @param b
     */
    public void updateNameVisibility(boolean b) {
        entities.stream().filter(uuid -> Bukkit.getEntity(uuid) instanceof ArmorStand)
                .forEach(uuid -> Bukkit.getEntity(uuid).setCustomNameVisible(b));
    }


    public String getName() {
        return name;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    //Only RP Manager plugins
//    @EventHandler
//    public void onTeleport(PlayerTeleportEvent event) {
//        if(!event.getPlayer().getUniqueId().equals(playerId)) return;
//
//        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
//        rpManager.removeRpPlayer(event.getPlayer());
////
////        rpManager.addRpPlayer(event.getPlayer());
//    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if(entities.contains(event.getEntity().getUniqueId()))
            Bukkit.getLogger().info("Got to dismount");

            RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
            rpManager.removeRpPlayer(Bukkit.getPlayer(playerId));
            //Calc distance to see if it was triggered by a teleport event
//            if(event.getEntity().getLocation().distance(Bukkit.getPlayer(playerId).getLocation()) > 5) {
//
//            } else {
//                event.setCancelled(true);
//            }


    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!event.getEntity().getUniqueId().equals(playerId)) return;

        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
        rpManager.removeRpPlayer(event.getEntity());
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for(Entity e: event.getEntities()) {
            if(entities.contains(e.getUniqueId())) {
                RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
                rpManager.removeRpPlayer(Bukkit.getPlayer(playerId));

                e.remove(); //final remove incase remove rp player returns null since Bukkit.getEntity is null at this point
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if(!event.getPlayer().getUniqueId().equals(playerId)) return;

        RPManager rpManager = ServiceLocator.getLocator().getService(RPManager.class);
        rpManager.removeRpPlayer(Bukkit.getPlayer(playerId));

        Bukkit.getLogger().info("Got to teleport event");

        event.getPlayer().teleport(event.getTo());
    }

}
