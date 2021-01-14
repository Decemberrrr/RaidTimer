package com.anotherdeveloper.raidtimer.Events;

import com.anotherdeveloper.raidtimer.RaidBlock;
import com.anotherdeveloper.raidtimer.RaidTimer;
import com.anotherdeveloper.raidtimer.Utils.ObjectSet;
import com.anotherdeveloper.raidtimer.Utils.TimeUtil;
import com.anotherdeveloper.raidtimer.Utils.XMaterial;
import com.massivecraft.factions.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RaidListener implements Listener {

    private RaidTimer plugin;

    public RaidListener(RaidTimer plugin) {
        this.plugin = plugin;
    }

    /**
     * Puts faction in Raid Block when tnt explodes
     */
    @EventHandler
    private void onExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT) {
            Location explosionLocation = event.getLocation();
            FLocation factionLocation = new FLocation(explosionLocation);
            Faction faction = Board.getInstance().getFactionAt(factionLocation);
            if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                return;
            }
            if (plugin.getRaidBlocked().containsKey(faction)) {
                plugin.getRaidBlocked().get(faction).setTimeRemainingInSeconds(plugin.getConfigFile().getInteger("settings.raid-block-time"));
                return;
            }

            Entity entity = event.getEntity();
            entity.getSour  

            new RaidBlock(plugin, faction, plugin.getConfigFile().getInteger("settings.raid-block-time"));
        }
    }


    @EventHandler
    private void onMine(BlockBreakEvent event) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();
            if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                return;
            }
            if (plugin.getRaidBlocked().containsKey(faction)) {
                if (!plugin.getConfigFile().getConfig().getBoolean("settings.allow-block-break")) {
                    plugin.sendMessage(player, "CANNOT-BREAK-BLOCK", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                    event.setCancelled(true);
                    return;
                }
                if (XMaterial.matchXMaterial(event.getBlock().getType()) == XMaterial.SPAWNER) {
                    if (!plugin.getConfigFile().getConfig().getBoolean("settings.allow-spawners-mined")) {
                        plugin.sendMessage(player, "CANNOT-MINE-SPAWNER", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                        event.setCancelled(true);
                    }
                }
            }
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        if (!plugin.getConfigFile().getConfig().getBoolean("settings.allow-block-placement")) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();
            if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                return;
            }
            if (plugin.getRaidBlocked().containsKey(faction)) {
                if (faction.getAllClaims().contains(new FLocation(player.getLocation()))) {
                    plugin.sendMessage(player, "CANNOT-PLACE-BLOCK", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onUseBucket(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();

            if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                return;
            }

            if (plugin.getRaidBlocked().containsKey(faction)) {
                if (Bukkit.getServer().getClass().getPackage().getName().contains("1_8")) {
                    if (player.getInventory().getItemInHand().getType() == Material.BUCKET
                        || player.getInventory().getItemInHand().getType() == Material.LAVA_BUCKET
                        || player.getInventory().getItemInHand().getType() == Material.WATER_BUCKET) {
                        if (faction.getAllClaims().contains(new FLocation(player.getLocation()))) {
                            plugin.sendMessage(player, "CANNOT-USE-BUCKET", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                            event.setCancelled(true);
                            return;
                        }
                    }
                    return;
                }
                if (player.getInventory().getItemInMainHand().getType() == Material.BUCKET
                        || player.getInventory().getItemInMainHand().getType() == Material.LAVA_BUCKET
                        || player.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET) {

                    if (faction.getAllClaims().contains(new FLocation(player.getLocation()))) {
                        plugin.sendMessage(player, "CANNOT-USE-BUCKET", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onSpawnerChest(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != null) {
                if (XMaterial.matchXMaterial(Objects.requireNonNull(event.getCurrentItem()).getType()) == XMaterial.SPAWNER) {
                    Player player = (Player) event.getWhoClicked();
                    FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                    Faction faction = fPlayer.getFaction();
                    if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                        return;
                    }
                    if (plugin.getRaidBlocked().containsKey(faction)) {
                        if (!plugin.getConfigFile().getConfig().getBoolean("settings.allow-chest-spawners")) {
                            plugin.sendMessage(player, "CANNOT-CHEST-SPAWNER", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
    }

    @EventHandler
    private void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = fPlayer.getFaction();
        if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
            return;
        }
        if (plugin.getRaidBlocked().containsKey(faction)) {
            for (String cmd : plugin.getConfigFile().getStringList("settings.blocked-commands")) {
                if (event.getMessage().startsWith(cmd)) {
                    plugin.sendMessage(player, "COMMAND-BLOCKED", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                    return;
                }
            }
        }
    }

    /**
    @EventHandler
    private void onTntInsert(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != null && event.getView().getTopInventory().getType() == InventoryType.DISPENSER) {
            if (XMaterial.matchXMaterial(Objects.requireNonNull(event.getCurrentItem()).getType()) == XMaterial.TNT) {
                Player player = (Player) event.getWhoClicked();
                NBTItem nbtTNT = new NBTItem(event.getCurrentItem());
                nbtTNT.clearCustomNBT();
                nbtTNT.setString("firedby", player.getName());
                nbtTNT.applyNBT(event.getCurrentItem());
                player.sendMessage("nbt set to: " + nbtTNT.getString("firedby"));
            }
        }
    }**/

}
