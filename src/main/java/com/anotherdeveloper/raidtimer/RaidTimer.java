package com.anotherdeveloper.raidtimer;

import com.anotherdeveloper.raidtimer.Commands.RaidBlockCmd;
import com.anotherdeveloper.raidtimer.Events.RaidListener;
import com.anotherdeveloper.raidtimer.Utils.Color;
import com.anotherdeveloper.raidtimer.Utils.DataFile;
import com.anotherdeveloper.raidtimer.Utils.ObjectSet;
import com.massivecraft.factions.Faction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class RaidTimer extends JavaPlugin {

    private final HashMap<Faction, RaidBlock> raidBlocked = new HashMap<>();
    private DataFile configFile;
    private DataFile messageFile;

    public void onEnable() {
        this.configFile = new DataFile(this, "config", true);
        this.messageFile = new DataFile(this, "messages", true);

        if(getServer().getPluginManager().getPlugin("SaberFactions") != null || getServer().getPluginManager().getPlugin("SavageFactions") != null || getServer().getPluginManager().getPlugin("factions") != null || getServer().getPluginManager().getPlugin("Factions") != null ) {
            System.out.println("RaidTimer: Plugin Loading...");
            registerCommands();
            registerListeners();
        } else {
            getServer().getPluginManager().disablePlugin(this);
            System.out.println("RaidTimer: No Factions Plugin Found!");
        }
    }

    public void onDisable() {

    }


    private void registerCommands() {
        getServer().getPluginCommand("raidblock").setExecutor(new RaidBlockCmd(this));
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new RaidListener(this), this);
    }

    public void sendMessage(CommandSender player, String msg, ObjectSet... replacements) {
        sendMessage(player, msg, false, replacements);
    }

    public void sendMessage(CommandSender player, String msg, boolean actionBar, ObjectSet... replacements) {
        if (getMessageFile().getConfig().isString("messages." + msg)) {
            if (actionBar && player instanceof Player) {
                // Todo
            } else {
                player.sendMessage(getMessage(msg, replacements));
            }
        } else {
            List<String> messageToTranslate = getMessageFile().getStringList("messages." + msg);
            if (messageToTranslate == null || messageToTranslate.isEmpty()) {
                player.sendMessage(Color.color("&cNo message found for key: &d" + msg));
                return;
            }
            for (String s : messageToTranslate) {
                for (ObjectSet replacement : replacements) {
                    s = s.replace(replacement.getA().toString(), replacement.getB().toString());
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }

    public String getMessage(String msg, ObjectSet... replacements) {
        String messageToTranslate = getMessageFile().getString("messages." + msg);
        if (messageToTranslate == null || messageToTranslate.isEmpty()) {
            return Color.color("&cNo message found for key: &d" + msg);
        }
        for (ObjectSet replacement : replacements) {
            messageToTranslate = messageToTranslate.replace(replacement.getA().toString(), replacement.getB().toString());
        }
        return ChatColor.translateAlternateColorCodes('&', messageToTranslate);
    }


}
