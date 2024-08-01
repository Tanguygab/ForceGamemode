package io.github.tanguygab.forcegamemode;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GMListener implements Listener {

    private final ForceGamemode plugin;

    private final GameMode defaultGamemode;
    private final Map<String,GameMode> worldsGamemodes = new HashMap<>();
    private final List<String> preventSwitch;

    private final String cantSwitchMsg;

    public GMListener(ForceGamemode plugin) {
        this.plugin = plugin;
        String defGamemode = plugin.getConfig().getString("default-gamemode","NONE");

        defaultGamemode = getGamemode(defGamemode,null);

        ConfigurationSection worldsMap = plugin.getConfig().getConfigurationSection("worlds");
        if (worldsMap != null) worldsMap.getValues(false).forEach((world, gamemode) -> worldsGamemodes.put(world, getGamemode((String) gamemode, world)));

        preventSwitch = plugin.getConfig().getStringList("worlds-prevent-switching");
        cantSwitchMsg = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("cant-switch-message", "&cYou can't change gamemode in this world!"));
    }

    private GameMode getGamemode(String gamemode, String world) {
        if (gamemode == null || gamemode.equalsIgnoreCase("NONE")) return null;
        try {
            return GameMode.valueOf(gamemode.trim().toUpperCase());
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid gamemode \"" + gamemode + (world == null ? "" : "\" for world \"" + world) + "\". Skipping...");
            return null;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateGamemode(e.getPlayer());
    }

    @EventHandler
    public void onWorldSwitch(PlayerChangedWorldEvent e) {
        updateGamemode(e.getPlayer());
    }

    private void updateGamemode(Player player) {
        String world = player.getWorld().getName();
        GameMode expected = worldsGamemodes.getOrDefault(world, defaultGamemode);
        if (expected == null || expected == player.getGameMode()) return;
        player.setGameMode(expected);
    }

    @EventHandler
    public void onGamemodeSwitch(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        String world = player.getWorld().getName();
        GameMode expected = worldsGamemodes.getOrDefault(world, defaultGamemode);
        if (expected == null || expected == e.getNewGameMode()) return;

        if (preventSwitch.contains("*") || preventSwitch.contains(world)) {
            if (expected != player.getGameMode()) player.setGameMode(expected);
            if (!cantSwitchMsg.isEmpty()) player.sendMessage(cantSwitchMsg);
            e.setCancelled(true);
        }
    }


}
