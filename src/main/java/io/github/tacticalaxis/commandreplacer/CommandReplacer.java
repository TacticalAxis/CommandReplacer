package io.github.tacticalaxis.commandreplacer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public class CommandReplacer extends JavaPlugin implements Listener {

    private static CommandReplacer instance;
    private static SimpleCommandMap scm;

    @Override
    public void onEnable() {
        instance = this;
        setupSimpleCommandMap();
        ConfigurationManager.getInstance().setupConfiguration();
        getServer().getPluginManager().registerEvents(this, this);
        activateCMDs();
    }

    @EventHandler
    public void cmd(PlayerCommandPreprocessEvent event) {
        ConfigurationSection cfg = ConfigurationManager.getInstance().getMainConfiguration();
        String name = event.getMessage().substring(1).toLowerCase().split(" ")[0];
        ArrayList<String> command = new ArrayList<>(Arrays.asList(event.getMessage().substring(1).toLowerCase().split(" ")));
        ArrayList<String> args = new ArrayList<>();
        StringBuilder toExecute = new StringBuilder();
        boolean used = false;
        int test = command.size();
        for (int i = 0; i < test; i++) {
//            System.out.println("CURRENT CMD (" + command.size() + "): " + Arrays.toString(command.toArray()));
            for (String cmd : cfg.getKeys(false)) {
//                System.out.println("CONFIG: " + Arrays.toString(cmd.split("-")));
                if (Arrays.toString(command.toArray()).equalsIgnoreCase(Arrays.toString(cmd.split("-")))) {
                    used = true;
                    toExecute = new StringBuilder(cfg.getString(cmd).toLowerCase() + " ");
                    for (String a : args) {
                        toExecute.append(a).append(" ");
                    }
                }
            }
            ArrayList<String> tmp = new ArrayList<>(args);
            args = new ArrayList<>();
            args.add(command.get(command.size() - 1));
            command.remove(command.size() - 1);
            args.addAll(tmp);
        }
        if (!used) {
            boolean isReal = false;
            for (String cmd : cfg.getKeys(false)) {
                if (cmd.toLowerCase().split("-")[0].equalsIgnoreCase(name)) {
                    isReal = true;
                    break;
                }
            }
            if (isReal) {
                event.getPlayer().sendMessage(ChatColor.RED + "Invalid command. See options below");
                for (String cmd : cfg.getKeys(false)) {
                    if (cmd.split("-")[0].equalsIgnoreCase(name)) {
                        event.getPlayer().sendMessage(ChatColor.GOLD + "/" + cmd.replace("-", " "));
                    }
                }
            }
        } else {
            Bukkit.dispatchCommand(event.getPlayer(), toExecute.toString().trim());
        }
    }

    private void activateCMDs() {
        for (String cmd : ConfigurationManager.getInstance().getMainConfiguration().getKeys(false)) {
            registerCommands(new BaseCommand(cmd.split("-")[0]));
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static CommandReplacer getInstance() {
        return instance;
    }

    private void registerCommands(CustomCommand... commands) {
        //Register the plugin
        for (CustomCommand command : commands) {
            scm.register("CommandReplacer", command);
        }
    }

    private void setupSimpleCommandMap() {
        SimplePluginManager spm = (SimplePluginManager) this.getServer().getPluginManager();
        Field f = null;
        try {
            f = SimplePluginManager.class.getDeclaredField("commandMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (f != null) {
            f.setAccessible(true);
        }
        try {
            if (f != null) {
                scm = (SimpleCommandMap) f.get(spm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}