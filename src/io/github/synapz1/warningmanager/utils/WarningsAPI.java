package io.github.synapz1.warningmanager.utils;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.WarningManager;
import io.github.synapz1.warningmanager.storage.WarningsFile;
import io.github.synapz1.warningmanager.storage.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class WarningsAPI {

    private WarningsFile warnings = SettingsManager.getManager().getWarningsFile();

    private static WarningsAPI instance = new WarningsAPI();

    public static WarningsAPI getWarningsAPI() {
        return instance;
    }

    public void addWarning(CommandSender sender, UUID p, String reason, String type) {
        type = type.toLowerCase();

        if (reason.length() <= 0)
            reason = SettingsManager.DEFAULT_REASON.replace("%punishment%", type);

        setWarnings(p, 1);
        setType(p, type);
        setReason(p, reason);
        setSender(p, sender);
        setMilis(p, System.currentTimeMillis());
        logDate(p);

        String broadcastMessage = SettingsManager.BROADCAST_MESSAGE.replace("%SENDER%", sender.getName());
        broadcastMessage = broadcastMessage.replace("%PLAYER%", Bukkit.getOfflinePlayer(p).getName());
        broadcastMessage = broadcastMessage.replace("%REASON%", reason);

        Messenger.getMessenger().broadcastMessage(broadcastMessage);

        String playerMessage = SettingsManager.PLAYER_MESSAGE;
        if (!isNone(playerMessage)) {
            playerMessage = playerMessage.replace("%SENDER%", sender.getName());
            playerMessage = playerMessage.replace("%REASON%", reason);
            playerMessage = playerMessage.replace("%WARNINGS%", getWarningsInt(p) + "");

            Utils.tryToSendPlayerMessage(playerMessage, p);
        }

        if (SettingsManager.getManager().getPunishments(type).contains(Integer.valueOf(getPunishmentAmount(p)))) {
            String punishment = SettingsManager.getManager().getPunishmentCommand(type, getWarningsInt(p));
            punishment = punishment.replace("%PLAYER%", p.toString());
            punishment = punishment.replace("%SENDER%", sender.getName());
            punishment = punishment.replace("%REASON%", reason);
            punishment = punishment.replace("%WARNINGS%", getWarningsInt(p) + "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishment);
        }

        if (!SettingsManager.MYSQL_ENABLED) return;
        try
        {
            DatabaseManager.getManager().getDatabase().updateDatabase(warnings.getFileConfig());
            Messenger.getMessenger().message(Bukkit.getConsoleSender(), "Warning for " + p + " added to the database.");
        } catch (SQLException e)
        {
            Messenger.getMessenger().message(Bukkit.getConsoleSender(), "Could not add warning for " + p + " to the database.");
            e.printStackTrace();
        }
    }

    public void setMilis(UUID p, long milis) {
        String path = warnings.getPath(getStartPath(p), getWarningsInt(p) + "", "Millis");

        warnings.setValue(path, milis);
    }

    public void setWarnings(UUID p, int amount) {
        String path = warnings.getPath(getStartPath(p), "Total-Warnings");

        warnings.setValue(path, getWarningsInt(p) + amount);
    }

    public int getWarningsInt(UUID p) {
        String path = warnings.getPath(getStartPath(p), "Total-Warnings");

        return (Integer) (warnings.getValue(path) == null ? 0 : warnings.getValue(path));
    }

    public void setType(UUID p, String type) {
        String path = warnings.getPath(getStartPath(p), getWarningsInt(p) + "", "Type");

        warnings.setValue(path, type);
    }

    public String getType(UUID p, int warningNumber) {
        return (String) warnings.getValue(warnings.getPath(getStartPath(p), "" + warningNumber, "Type"));
    }

    public long getMillis(UUID p, int warningNumber) {
        return warnings.getFileConfig().getLong(warnings.getPath(getStartPath(p), "" + warningNumber, "Millis"));
    }

    public void setReason(UUID p, String reason) {
        String path = warnings.getPath(getStartPath(p), getWarningsInt(p) + "", "Reason");

        warnings.setValue(path, reason);
    }

    public String getReason(UUID p, int reasonNumber) {
        return (String) warnings.getValue(warnings.getPath(getStartPath(p), "" + reasonNumber, "Reason"));
    }

    public void setSender(UUID p, CommandSender sender) {
        warnings.setValue(warnings.getPath(getStartPath(p), getWarningsInt(p) + "", "Sender"), sender.getName());
    }

    public String getSender(UUID p, int reasonNumber) {
        return (String) warnings.getValue(warnings.getPath(getStartPath(p), "" + reasonNumber, "Sender"));
    }

    public void logDate(UUID p) {
        warnings.setValue(warnings.getPath(getStartPath(p), "" + getWarningsInt(p), "Date"), Calendar.getInstance().getTime());
    }

    public String[] getDate(UUID player, int warning) {
        String[] timeFormates = new String[2];
        Date toFormat = (Date) warnings.getValue(warnings.getPath(getStartPath(player), warning + "", "Date"));

        SimpleDateFormat dateFormat = new SimpleDateFormat(SettingsManager.getManager().getDateFormat());

        timeFormates[0] = dateFormat.format(toFormat);

        SimpleDateFormat timeFormat;

        if (SettingsManager.militaryTime) {
            timeFormat = new SimpleDateFormat("HH:mm:ss zzz");
        } else {
            timeFormat = new SimpleDateFormat("hh:mm:ss a zzz");
        }
        timeFormates[1] = timeFormat.format(toFormat);

        return timeFormates;
    }

    public boolean isInRange(UUID player, int warning) {
        long millis = System.currentTimeMillis() - getMillis(player, warning);
        int days = (int) (millis / (1000*60*60*24));

        return days <= SettingsManager.warningAge;
    }

    public void reset(UUID p) {
        warnings.setValue(p.toString(), null);
    }

    public void check(CommandSender sender, UUID p) {
        sender.sendMessage(GRAY + "" + STRIKETHROUGH + BOLD + Utils.makeSpaces(15) + DARK_GRAY + BOLD + STRIKETHROUGH + "[-" + RESET + GOLD + " " + BOLD + Bukkit.getOfflinePlayer(p).getName() + " " + DARK_GRAY + BOLD + STRIKETHROUGH + "-]" + GRAY + BOLD + STRIKETHROUGH + Utils.makeSpaces(15));
        for (int i = 1; i <= 50; i++) {

            if ((getSender(p, i) != null) && (getReason(p, i) != null) && warnings.getValue(getStartPath(p)) != null) {
                sender.sendMessage(ChatColor.GOLD + "Warning #" + i + ": ");
                sender.sendMessage(ChatColor.GOLD + "  Type: " + ChatColor.RED + getType(p, i));
                sender.sendMessage(ChatColor.GOLD + "  Reason: " + ChatColor.RED + getReason(p, i));
                sender.sendMessage(ChatColor.GOLD + "  Sender: " + ChatColor.RED + getSender(p, i));
                sender.sendMessage(ChatColor.GOLD + "  Date: " + ChatColor.RED + getDate(p, i)[0]);
                sender.sendMessage(ChatColor.GOLD + "  Time: " + ChatColor.RED + getDate(p, i)[1]);
            }

        }
        sender.sendMessage(ChatColor.GOLD + "Total Warnings: " + ChatColor.RED + getWarningsInt(p));
    }

    public int getPunishmentAmount(UUID p) {
        int amount = 0;
        for (int i = 1; i <= 50; i++) {

            if ((getSender(p, i) != null) && (getReason(p, i) != null) && warnings.getValue(getStartPath(p)) != null && isInRange(p, i)) {
                amount++;
            }

        }
        return amount;
    }

    public void remove(CommandSender sender, UUID player, String type, int warning) {
        if (getSender(player, warning) != null) {
            warnings.setValue(warnings.getPath(getStartPath(player), String.valueOf(warning)), null);

            sender.sendMessage(ChatColor.GOLD + "You removed warning " + ChatColor.RED + warning + ChatColor.GOLD + " from " + ChatColor.RED + player);
            setWarnings(player,  -1);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.RED + player + ChatColor.GOLD + " does not have warning " + ChatColor.RED + warning);
        }
    }

    public void notifyOnReset(CommandSender sender, String target) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.isOp()) || (p.hasPermission("warnings.notify"))) {
                if (!sender.getName().equals(p.getName())) {
                    p.sendMessage(ChatColor.GOLD + "Please be aware that " + ChatColor.RED + sender.getName() + ChatColor.GOLD + " has reset " + ChatColor.RED + target + ChatColor.GOLD + "'s warnings.");
                }
            }
        }
    }

    private boolean isNone(String toBeChecked) {
        if (toBeChecked.equals("none")) {
            return true;
        }
        return false;
    }

    private String getStartPath(UUID player) {
        return warnings.getPath(player.toString());
    }
}