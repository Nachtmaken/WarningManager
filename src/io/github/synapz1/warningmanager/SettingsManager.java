package io.github.synapz1.warningmanager;

import io.github.synapz1.warningmanager.utils.Messenger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SettingsManager {
    private static SettingsManager instance = new SettingsManager();
    private FileConfiguration warnings;
    private File wFile;
    private WarningManager wm = null;
    private static ArrayList<Integer> punishments = new ArrayList();
    public static String PREFIX;
    public static String DEFAULT_REASON;
    public static String BROADCAST_MESSAGE;
    public static String PLAYER_MESSAGE;
    public static boolean broadcast;
    public static boolean militaryTime;

    public static SettingsManager getManager() {
        return instance;
    }

    public void init(WarningManager wm) {
        if (!wm.getDataFolder().exists()) {
            wm.getDataFolder().mkdir();
        }
        this.wFile = new File(wm.getDataFolder(), "warnings.yml");
        if (!this.wFile.exists()) {
            try {
                this.wFile.createNewFile();
            } catch (IOException e) {
                Messenger.getMessenger().message(Bukkit.getConsoleSender(), new String[]{"Could not save warnings.yml"});
                e.printStackTrace();
            }
        }

        boolean loadConfig = true;

        for (File file : wm.getDataFolder().listFiles()) {
            if (file.getName().equals("config.yml"))
                loadConfig = false;
        }

        if (loadConfig)
            wm.saveResource("config.yml", false);

        this.warnings = YamlConfiguration.loadConfiguration(this.wFile);

        loadValues(wm.getConfig());
        loadPunishments(wm.getConfig());
        this.wm = wm;
    }

    public void saveFiles() {
        try {
            this.warnings.save(this.wFile);
        } catch (Exception e) {
            Messenger.getMessenger().message(Bukkit.getConsoleSender(), new String[]{ChatColor.RED + "Could not save warnings.yml!"});
            e.printStackTrace();
        }
    }

    public String getDateFormat() {
        String finalOutput = this.wm.getConfig().getString("date-format");
        finalOutput = finalOutput.replace("%WEEKDAY%", "E");
        finalOutput = finalOutput.replace("%MM%", "MM");
        finalOutput = finalOutput.replace("%DD%", "dd");
        finalOutput = finalOutput.replace("%YY%", "yyyy");
        return finalOutput;
    }

    public FileConfiguration getWarningFile() {
        return this.warnings;
    }

    public ArrayList<Integer> getPunishments() {
        return punishments;
    }

    public String getPunishmentCommand(int punishmentNumber) {
        return this.wm.getConfig().getString("punishments." + punishmentNumber + ".command");
    }

    public void loadValues(FileConfiguration file) {
        militaryTime = file.getBoolean("military-time");
        broadcast = file.getBoolean("broadcast-reason");
        BROADCAST_MESSAGE = transColors(file.getString("broadcast-message"));
        PREFIX = transColors(file.getString("prefix"));
        DEFAULT_REASON = transColors(file.getString("default-reason"));
        PLAYER_MESSAGE = transColors(file.getString("player-message"));
    }

    private String transColors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private void loadPunishments(FileConfiguration file) {
        for (int i = 1; i <= 100; i++) {
            if (file.getString("punishments." + i + ".command") != null) {
                punishments.add(Integer.valueOf(i));
            }
        }
    }
}