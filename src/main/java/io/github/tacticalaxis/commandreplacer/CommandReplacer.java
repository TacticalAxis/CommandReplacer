package io.github.tacticalaxis.commandreplacer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class CommandReplacer extends JavaPlugin implements CommandExecutor, Listener {

    private static CommandReplacer instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigurationManager.getInstance().setupConfiguration();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("cr").setExecutor(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static CommandReplacer getInstance() {
        return instance;
    }

    @EventHandler
    public void cmd(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().length() > 1) {
            String command = event.getMessage().substring(1).toLowerCase();
            if (getCMD(command) != null) {
                event.setCancelled(true);
                event.getPlayer().performCommand(getCMD(command) + " " + String.join(" ", getARGS(command)));
            }
        }
        System.out.println(event.getMessage());
    }

    private String getCMD(String playerEntry) {
        for (String s : ConfigurationManager.getInstance().getMainConfiguration().getKeys(false)) {
            if (s.equalsIgnoreCase(playerEntry.split(" ")[0])) {
                return ConfigurationManager.getInstance().getMainConfiguration().getString(s);
            }
        }
        return null;
    }

    private String[] getARGS(String playerEntry) {
        ArrayList<String> args = new ArrayList<>();
        if (playerEntry.split(" ").length > 1) {
            int num = 0;
            for (String s : playerEntry.split(" ")) {
                if (num != 0) {
                    args.add(s);
                }
                num += 1;
            }
        }
        System.out.println(args);
        return args.toArray(new String[0]);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ConfigurationManager.getInstance().reloadConfiguration();
        sender.sendMessage(ChatColor.GREEN + "CommandReplacer config reloaded");
        return true;
    }
}