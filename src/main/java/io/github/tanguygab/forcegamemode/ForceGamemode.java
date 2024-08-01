package io.github.tanguygab.forcegamemode;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public final class ForceGamemode extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(new GMListener(this), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            onDisable();
            onEnable();
            sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
        }
        return true;
    }
}
