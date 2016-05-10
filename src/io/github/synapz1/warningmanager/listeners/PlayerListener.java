package io.github.synapz1.warningmanager.listeners;

import io.github.synapz1.warningmanager.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Created by Jeremy(Refrigerbater) on 5/9/2016.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = SettingsManager.getManager().getOfflineWarningsFile().getFileConfig();
        if (config.contains("Players." + player.getName().toLowerCase() + ".Messages")) {
            String message = config.getString("Players." + player.getName().toLowerCase() + ".Messages");
            player.sendMessage(message);
            config.set("Players." + player.getName().toLowerCase(), null);
            SettingsManager.getManager().getOfflineWarningsFile().saveFile();
        }
    }
}
