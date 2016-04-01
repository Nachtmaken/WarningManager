package io.github.synapz1.warningmanager.utils;

import io.github.synapz1.warningmanager.SettingsManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.GOLD;

public class WarningsAPI {
    FileConfiguration file = SettingsManager.getManager().getWarningFile();
    private static WarningsAPI instance = new WarningsAPI();

    public static WarningsAPI getWarningsAPI() {
        return instance;
    }

    public void addWarning(CommandSender sender, String p, String reason) {
        setWarnings(p, 1);
        setReason(p, reason);
        setSender(p, sender);
        logDate(p);

        String broadcastMessage = SettingsManager.BROADCAST_MESSAGE.replace("%SENDER%", sender.getName());
        broadcastMessage = broadcastMessage.replace("%PLAYER%", p);
        broadcastMessage = broadcastMessage.replace("%REASON%", reason);

        Messenger.getMessenger().broadcastMessage(broadcastMessage);

        String playerMessage = SettingsManager.PLAYER_MESSAGE;
        if (!isNone(playerMessage)) {
            playerMessage = playerMessage.replace("%SENDER%", sender.getName());
            playerMessage = playerMessage.replace("%REASON%", reason);
            playerMessage = playerMessage.replace("%WARNINGS%", getWarningsInt(p) + "");

            Utils.tryToSendPlayerMessage(playerMessage, p);
        }
        if (SettingsManager.getManager().getPunishments().contains(Integer.valueOf(getWarningsInt(p)))) {
            String punishment = SettingsManager.getManager().getPunishmentCommand(getWarningsInt(p));
            punishment = punishment.replace("%PLAYER%", p);
            punishment = punishment.replace("%SENDER%", sender.getName());
            punishment = punishment.replace("%REASON%", reason);
            punishment = punishment.replace("%WARNINGS%", getWarningsInt(p) + "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishment);
        }
    }

    public void setWarnings(String p, int amount) {
        int newTotalWarnings = getWarningsInt(p) + amount;
        this.file.set(p.toLowerCase() + ".Total-Warnings", Integer.valueOf(newTotalWarnings));
        SettingsManager.getManager().saveFiles();
    }

    public int getWarningsInt(String p) {
        return this.file.getInt(p.toLowerCase() + ".Total-Warnings");
    }

    public void setReason(String p, String reason) {
        this.file.set(p.toLowerCase() + ".Warning" + getWarningsInt(p) + ".Reason", reason);
        SettingsManager.getManager().saveFiles();
    }

    public String getReason(String p, int reasonNumber) {
        return this.file.getString(p.toLowerCase() + ".Warning" + reasonNumber + ".Reason");
    }

    public void setSender(String p, CommandSender sender) {
        this.file.set(p.toLowerCase() + ".Warning" + getWarningsInt(p) + ".Sender", sender.getName());
        SettingsManager.getManager().saveFiles();
    }

    public String getSender(String p, int reasonNumber) {
        return this.file.getString(p.toLowerCase() + ".Warning" + reasonNumber + ".Sender");
    }

    public void logDate(String p) {
        this.file.set(p.toLowerCase() + ".Warning" + getWarningsInt(p) + ".Date", Calendar.getInstance().getTime());
        SettingsManager.getManager().saveFiles();
    }

    public String[] getDate(String player, int warning) {
        String[] timeFormates = new String[2];
        Date toFormat = (Date) this.file.get(player.toLowerCase() + ".Warning" + warning + ".Date");

        SimpleDateFormat dateFormat = new SimpleDateFormat(SettingsManager.getManager().getDateFormat());

        timeFormates[0] = dateFormat.format(toFormat);

        SimpleDateFormat timeFormat = null;
        if (SettingsManager.militaryTime) {
            timeFormat = new SimpleDateFormat("HH:mm:ss zzz");
        } else {
            timeFormat = new SimpleDateFormat("hh:mm:ss a zzz");
        }
        timeFormates[1] = timeFormat.format(toFormat);

        return timeFormates;
    }

    public void reset(String p) {
        this.file.set(p.toLowerCase(), null);
        SettingsManager.getManager().saveFiles();
    }

    public void check(CommandSender sender, String p) {
        int warningsAmount = getWarningsInt(p);

        sender.sendMessage(GRAY + "" + STRIKETHROUGH + BOLD + Utils.makeSpaces(15) + DARK_GRAY + BOLD + STRIKETHROUGH + "[-" + RESET + GOLD + " " + BOLD + p + " " + DARK_GRAY + BOLD + STRIKETHROUGH + "-]" + GRAY + BOLD + STRIKETHROUGH + Utils.makeSpaces(15));
        for (int i = 1; i <= warningsAmount + 40; i++) {
            if ((getSender(p, i) != null) && (getReason(p, i) != null)) {
                sender.sendMessage(ChatColor.GOLD + "Warning #" + i + ": ");
                sender.sendMessage(ChatColor.GOLD + "  Reason: " + ChatColor.RED + getReason(p, i));
                sender.sendMessage(ChatColor.GOLD + "  Sender: " + ChatColor.RED + getSender(p, i));
                sender.sendMessage(ChatColor.GOLD + "  Date: " + ChatColor.RED + getDate(p, i)[0]);
                sender.sendMessage(ChatColor.GOLD + "  Time: " + ChatColor.RED + getDate(p, i)[1]);
            }
        }
        sender.sendMessage(ChatColor.GOLD + "Total Warnings: " + ChatColor.RED + getWarningsInt(p));
    }

    public void remove(CommandSender sender, String player, int warning) {
        if (getSender(player, warning) != null) {
            this.file.set(player.toLowerCase() + ".Warning" + warning, null);
            sender.sendMessage(ChatColor.GOLD + "You removed warning " + ChatColor.RED + warning + ChatColor.GOLD + " from " + ChatColor.RED + player);
            setWarnings(player, -1);
            SettingsManager.getManager().saveFiles();
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
}