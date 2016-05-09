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
        if (config.contains("Players." + player.getName() + ".Messages")) {
            List<String> messages = config.getStringList("Players." + player.getName() + ".Messages");
            String[] messagesToSend = new String[messages.size()];
            for (int i = 0; i < messagesToSend.length; i++) {
                messagesToSend[i] = ChatColor.translateAlternateColorCodes('&', messages.get(i));
            }
            player.sendMessage(messagesToSend);
            config.set("Players." + player.getName(), null);
        }
    }
}
