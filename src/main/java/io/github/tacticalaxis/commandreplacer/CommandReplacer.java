package io.github.tacticalaxis.commandreplacer;

import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;

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

    private void activateCMDs() {
        for (String cmd : ConfigurationManager.getInstance().getMainConfiguration().getKeys(false)) {
            registerCommands(new BaseCommand(cmd));
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
        Arrays.stream(commands).forEach(command -> scm.register("CommandReplacer", command));//Register the plugin
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