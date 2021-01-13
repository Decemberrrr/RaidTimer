package com.anotherdeveloper.raidtimer.Commands;

import com.anotherdeveloper.raidtimer.RaidBlock;
import com.anotherdeveloper.raidtimer.RaidTimer;
import com.anotherdeveloper.raidtimer.Utils.ObjectSet;
import com.anotherdeveloper.raidtimer.Utils.TimeUtil;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.Raid;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RaidBlockCmd implements CommandExecutor {

    private RaidTimer plugin;

    public RaidBlockCmd(RaidTimer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("raidblock")) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            Faction faction = fPlayer.getFaction();
            if (faction == null || faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
                plugin.sendMessage(player, "ERROR-NO-FACTION");
                return true;
            }
            if (plugin.getRaidBlocked().containsKey(faction)) {
                plugin.sendMessage(player, "COMMAND-RAID-BLOCK", new ObjectSet("{timeleft}", TimeUtil.formatTime(plugin.getRaidBlocked().get(faction).getTimeRemainingInSeconds())));
                return true;
            }
        }

        return false;
    }
}
