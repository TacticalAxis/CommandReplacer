package io.github.tacticalaxis.commandreplacer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaseCommand extends CustomCommand {

    public BaseCommand(String cmd) {
        super(cmd);
    }

    @Override
    public void run(CommandSender s, String cl, String[] args) {

        StringBuilder concatArgs = new StringBuilder();
        for (String a : args) {
            concatArgs.append(a).append(" ");
        }
        String joinedArgs = concatArgs.toString().trim();

        if (getCMD(cl) != null) {
            String fullCommand = getCMD(cl) + " " + joinedArgs.trim();
            if (s instanceof Player) {
                Player player = (Player) s;
                player.performCommand(fullCommand);
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), fullCommand);
            }
        } else {
            s.sendMessage(ChatColor.RED + "The command " + ChatColor.GOLD + cl + ChatColor.RED + " does not exist!");
        }
    }

    private String getCMD(String commandEntry) {
        for (String s : ConfigurationManager.getInstance().getMainConfiguration().getKeys(false)) {
            if (s.equalsIgnoreCase(commandEntry.split(" ")[0])) {
                return ConfigurationManager.getInstance().getMainConfiguration().getString(s);
            }
        }
        return null;
    }
}