package com.anotherdeveloper.raidtimer.Events;

import com.anotherdeveloper.raidtimer.RaidBlock;
import com.anotherdeveloper.raidtimer.RaidTimer;
import com.anotherdeveloper.raidtimer.Utils.ObjectSet;
import com.anotherdeveloper.raidtimer.Utils.TimeUtil;
import com.massivecraft.factions.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
                if (event.getBlock().getType() == Material.SPAWNER) {
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
            if (player.getInventory().getItemInMainHand().getType() == Material.BUCKET
                    || player.getInventory().getItemInMainHand().getType() == Material.LAVA_BUCKET
                    || player.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                Faction faction = fPlayer.getFaction();
                if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                    return;
                }
                if (plugin.getRaidBlocked().containsKey(faction)) {
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
            if (Objects.requireNonNull(event.getCurrentItem()).getType() == Material.SPAWNER) {
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

}
