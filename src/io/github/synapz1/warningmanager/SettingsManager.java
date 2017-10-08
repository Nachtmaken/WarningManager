package io.github.synapz1.warningmanager;

import io.github.synapz1.warningmanager.storage.OfflineWarningsFile;
import io.github.synapz1.warningmanager.storage.WarningsFile;
import io.github.synapz1.warningmanager.storage.database.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class SettingsManager {

    private static SettingsManager instance = new SettingsManager();
    private WarningManager wm = null;
    private WarningsFile warningsFile;
    private OfflineWarningsFile offlineWarningsFile;

    private static Map<String, List<Integer>> punishments = new HashMap<String, List<Integer>>();
    public static String PREFIX;
    public static String DEFAULT_REASON;
    public static String BROADCAST_MESSAGE;
    public static String PLAYER_MESSAGE;
    public static boolean broadcast;
    public static boolean militaryTime;
    public static int warningAge;

    public static boolean MYSQL_ENABLED;
    public static String HOST;
    public static String DATABASE;
    public static String WARNINGS_TABLE;
    public static String USERNAME;
    public static String PASSWORD;
    public static int PORT;

    public static SettingsManager getManager() {
        return instance;
    }

    public void init(WarningManager wm) {
        if (!wm.getDataFolder().exists())
            wm.getDataFolder().mkdir();

        boolean loadConfig = true;

        for (File file : wm.getDataFolder().listFiles()) {
            if (file.getName().equals("config.yml"))
                loadConfig = false;
        }

        if (loadConfig)
            wm.saveResource("config.yml", false);

        loadValues(wm.getConfig());
        this.wm = wm;

        DatabaseManager.getManager().init();

        warningsFile = new WarningsFile(wm);
        offlineWarningsFile = new OfflineWarningsFile(wm);
    }


    public String getDateFormat() {
        String finalOutput = this.wm.getConfig().getString("date-format");
        finalOutput = finalOutput.replace("%WEEKDAY%", "E");
        finalOutput = finalOutput.replace("%MM%", "MM");
        finalOutput = finalOutput.replace("%DD%", "dd");
        finalOutput = finalOutput.replace("%YY%", "yyyy");
        return finalOutput;
    }

    public List<Integer> getPunishments(String type) {
        return punishments.get(type);
    }

    public String getPunishmentCommand(String type, int punishmentNumber) {
        return this.wm.getConfig().getString("punishments." + type + "." + punishmentNumber + ".command");
    }

    public void loadValues(FileConfiguration file) {
        militaryTime = file.getBoolean("military-time");
        broadcast = file.getBoolean("broadcast-reason");
        BROADCAST_MESSAGE = transColors(file.getString("broadcast-message"));
        PREFIX = transColors(file.getString("prefix"));
        DEFAULT_REASON = transColors(file.getString("default-reason"));
        PLAYER_MESSAGE = transColors(file.getString("player-message"));
        warningAge = file.getInt("warning-age");

        MYSQL_ENABLED = file.getBoolean("mysql.enabled");
        HOST = file.getString("mysql.host");
        DATABASE = file.getString("mysql.database");
        WARNINGS_TABLE = file.getString("mysql.table");
        USERNAME = file.getString("mysql.username");
        PASSWORD = file.getString("mysql.password");
        PORT = file.getInt("mysql.port");

        loadPunishments(file);
    }

    public WarningsFile getWarningsFile() {
        return warningsFile;
    }

    public OfflineWarningsFile getOfflineWarningsFile() {
        return offlineWarningsFile;
    }

    private String transColors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static Map<String, List<Integer>> getPunishments() {
        return punishments;
    }

    private void loadPunishments(FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("punishments");

        if (section == null)
            return;

        Set<String> typeList = section.getValues(false).keySet();

        for (String type : typeList) {
            type = type.toLowerCase();
            List<Integer> typeInts = new ArrayList<Integer>();

            for (int i = 1; i <= 100; i++) {
                if (file.getString("punishments." + type + "." + i + ".command") != null) {
                    typeInts.add(i);
                }
            }

            punishments.put(type, typeInts);
        }
    }
}