package me.synapz.warnings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.synapz.warnings.utils.Messenger;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {

	private static SettingsManager instance = new SettingsManager();
	private FileConfiguration warnings;
    private File wFile;
    private WarningManager wm = null;
	private static ArrayList<Integer> punishments = new ArrayList<>();

	public static String PREFIX, DEFAULT_REASON, BROADCAST_MESSAGE, PLAYER_MESSAGE;
	public static boolean broadcast;
		
	private SettingsManager() {}
	
	
	public static SettingsManager getManager() {
		return instance;
	}
	
	public void init(WarningManager wm) {
        // Create a DataFolder is there isn't one already
		if (!wm.getDataFolder().exists()) {
            wm.getDataFolder().mkdir();
        }
		
        wFile = new File(wm.getDataFolder(), "warnings.yml");
        // Create the Warning File in case it is not there
        if (!wFile.exists()) {
            try {
                wFile.createNewFile();
            }
            catch (IOException e) {
                Messenger.getMessenger().message(Bukkit.getConsoleSender(), "Could not save warnings.yml");
                e.printStackTrace();
            }
        }

        wm.saveResource("config.yml", false);
        warnings = YamlConfiguration.loadConfiguration(wFile);

		// Load all values from the config into memory to be utilized by the plugin
		loadValues(wm.getConfig());
        loadPunishments(wm.getConfig());
        this.wm = wm;
	}
	
	public void saveFiles() {
        try {
            warnings.save(wFile);
        }catch (Exception e) {
            Messenger.getMessenger().message(Bukkit.getConsoleSender(), ChatColor.RED + "Could not save warnings.yml!");
            e.printStackTrace();
        }
	}
	
	public FileConfiguration getWarningFile() {
		return warnings;
	}

    public ArrayList<Integer> getPunishments() {
        return punishments;
    }

    public String getPunishmentCommand(int punishmentNumber) {
        return wm.getConfig().getString("punishments."+punishmentNumber+".command");
    }
	
	public void loadValues(FileConfiguration file) {
		broadcast         = file.getBoolean("broadcast-reason");
		BROADCAST_MESSAGE = transColors(file.getString("broadcast-message"));
		PREFIX            = transColors(file.getString("prefix"));
		DEFAULT_REASON    = transColors(file.getString("default-reason"));
		PLAYER_MESSAGE    = transColors(file.getString("player-message"));
	}
	
	private String transColors(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

    private void loadPunishments(FileConfiguration file) {
        for (int i = 1; i <= 100; i++) {
            if (file.getString("punishments."+i+".command") != null) {
                punishments.add(i);
            }
        }
    }
}
