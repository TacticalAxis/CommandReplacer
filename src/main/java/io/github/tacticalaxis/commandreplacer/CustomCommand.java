package io.github.tacticalaxis.commandreplacer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("NullableProblems")
public abstract class CustomCommand extends Command implements PluginIdentifiableCommand {
    CommandSender sender;
    CommandReplacer plugin = CommandReplacer.getInstance();

    protected CustomCommand(String name) {
        super(name);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    public abstract void run(CommandSender sender, String commandLabel, String[] arguments);

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] arguments) {
        this.sender = sender;
        run(sender, commandLabel, arguments);
        return true;
    }
}