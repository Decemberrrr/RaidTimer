package com.anotherdeveloper.raidtimer;


import com.anotherdeveloper.raidtimer.Utils.ObjectSet;
import com.anotherdeveloper.raidtimer.Utils.TimeUtil;
import com.massivecraft.factions.Faction;
import lombok.Getter;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RaidBlock extends BukkitRunnable {

    @Getter private int timeRemainingInSeconds;
    @Getter private boolean finished = false;
    @Getter private final Faction faction;
    private final RaidTimer plugin;

    /**
     * @param faction raid-blocked faction.
     * @param timeInSeconds time in seconds (Default: 5)
     */
    public RaidBlock(RaidTimer plugin, Faction faction, int timeInSeconds) {
        this.timeRemainingInSeconds = timeInSeconds;
        this.faction = faction;
        this.plugin = plugin;
        runTaskTimerAsynchronously(plugin, 20, 20);
        plugin.getRaidBlocked().put(faction, this);

        faction.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessage("RAID-BLOCK-STARTED", new ObjectSet("{timeleft}", TimeUtil.formatTime(timeInSeconds)))));
    }



    public void setTimeRemainingInSeconds(int timeRemainingInSeconds) {
        this.timeRemainingInSeconds = timeRemainingInSeconds;
    }

    @Override
    public void run() {
            if (timeRemainingInSeconds <= 0) {
                finished = true;
                plugin.getRaidBlocked().remove(faction);
                faction.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessage("RAID-BLOCK-OVER")));
                cancel();
            }
        timeRemainingInSeconds--;
    }
}