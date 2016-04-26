package io.github.synapz1.warningmanager.utils;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.storage.WarningsFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.bukkit.ChatColor.*;

public class WarningsAPI {

    WarningsFile warnings = SettingsManager.getManager().getWarningsFile();

    private static WarningsAPI instance = new WarningsAPI();

    public static WarningsAPI getWarningsAPI() {
        return instance;
    }

    public void addWarning(CommandSender sender, String p, String reason, String type) {
        p = p.toLowerCase();
        type = type.toLowerCase();

        if (reason.isEmpty())
            reason = SettingsManager.getManager().DEFAULT_REASON;

        setWarnings(p, type, 1);
        setReason(p, type, reason);
        setSender(p, type, sender);
        logDate(p, type);

        String broadcastMessage = SettingsManager.BROADCAST_MESSAGE.replace("%SENDER%", sender.getName());
        broadcastMessage = broadcastMessage.replace("%PLAYER%", p);
        broadcastMessage = broadcastMessage.replace("%REASON%", reason);

        Messenger.getMessenger().broadcastMessage(broadcastMessage);

        String playerMessage = SettingsManager.PLAYER_MESSAGE;
        if (!isNone(playerMessage)) {
            playerMessage = playerMessage.replace("%SENDER%", sender.getName());
            playerMessage = playerMessage.replace("%REASON%", reason);
            playerMessage = playerMessage.replace("%WARNINGS%", getWarningsInt(p, type) + "");

            Utils.tryToSendPlayerMessage(playerMessage, p);
        }
        if (SettingsManager.getManager().getPunishments(type).contains(Integer.valueOf(getWarningsInt(p, type)))) {
            String punishment = SettingsManager.getManager().getPunishmentCommand(type, getWarningsInt(p, type));
            punishment = punishment.replace("%PLAYER%", p);
            punishment = punishment.replace("%SENDER%", sender.getName());
            punishment = punishment.replace("%REASON%", reason);
            punishment = punishment.replace("%WARNINGS%", getWarningsInt(p, type) + "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishment);
        }
    }

    public void setWarnings(String p, String type, int amount) {
        String path = warnings.getPath(getStartPath(p, type), "Total-Warnings");

        warnings.setValue(path, getWarningsInt(p, type) + amount);
    }

    public int getWarningsInt(String p, String type) {
        String path = warnings.getPath(getStartPath(p, type), "Total-Warnings");

        return (Integer) (warnings.getValue(path) == null ? 0 : warnings.getValue(path));
    }

    public void setReason(String p, String type, String reason) {
        String path = warnings.getPath(getStartPath(p, type), getWarningsInt(p, type) + "", "Reason");

        warnings.setValue(path, reason);
    }

    public String getReason(String p, String type, int reasonNumber) {
        return (String) warnings.getValue(warnings.getPath(getStartPath(p, type), "" + reasonNumber, "Reason"));
    }

    public void setSender(String p, String type, CommandSender sender) {
        warnings.setValue(warnings.getPath(getStartPath(p, type), getWarningsInt(p, type) + "", "Sender"), sender.getName());
    }

    public String getSender(String p, String type, int reasonNumber) {
        return (String) warnings.getValue(warnings.getPath(getStartPath(p, type), "" + reasonNumber, "Sender"));
    }

    public void logDate(String p, String type) {
        warnings.setValue(warnings.getPath(getStartPath(p, type), "" + getWarningsInt(p, type), "Date"), Calendar.getInstance().getTime());
    }

    public String[] getDate(String player, String type, int warning) {
        String[] timeFormates = new String[2];
        Date toFormat = (Date) warnings.getValue(warnings.getPath(getStartPath(player, type), warning + "", "Date"));

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

    public void reset(String p) {
        warnings.setValue(p.toLowerCase(), null);
    }

    public void check(CommandSender sender, String p) {
        sender.sendMessage(GRAY + "" + STRIKETHROUGH + BOLD + Utils.makeSpaces(15) + DARK_GRAY + BOLD + STRIKETHROUGH + "[-" + RESET + GOLD + " " + BOLD + p + " " + DARK_GRAY + BOLD + STRIKETHROUGH + "-]" + GRAY + BOLD + STRIKETHROUGH + Utils.makeSpaces(15));
        for (String type : SettingsManager.getManager().getPunishments().keySet()) {
            sender.sendMessage(ChatColor.GOLD + type.replaceFirst(String.valueOf(type.toString().toCharArray()[0]), String.valueOf(type.toString().toUpperCase().toCharArray()[0])));

            try {
                warnings.getValue(getStartPath(p, type)).toString();
            } catch (NullPointerException exc) {
                sender.sendMessage(ChatColor.RED + "  No warnings found for this section.");
                continue;
            }

            for (int i = 1; i <= 50; i++) {

                if ((getSender(p, type, i) != null) && (getReason(p, type, i) != null) && warnings.getValue(getStartPath(p, type)) != null) {
                    sender.sendMessage(ChatColor.GOLD + "  Warning #" + i + ": ");
                    sender.sendMessage(ChatColor.GOLD + "    Reason: " + ChatColor.RED + getReason(p, type, i));
                    sender.sendMessage(ChatColor.GOLD + "    Sender: " + ChatColor.RED + getSender(p, type, i));
                    sender.sendMessage(ChatColor.GOLD + "    Date: " + ChatColor.RED + getDate(p, type, i)[0]);
                    sender.sendMessage(ChatColor.GOLD + "    Time: " + ChatColor.RED + getDate(p, type, i)[1]);
                }

            }
            sender.sendMessage(ChatColor.GOLD + "  Total Warnings: " + ChatColor.RED + getWarningsInt(p, type));
        }
    }

    public void remove(CommandSender sender, String player, String type, int warning) {
        if (getSender(player, type, warning) != null) {
            warnings.setValue(warnings.getPath(getStartPath(player, type), warning + ""), null);

            sender.sendMessage(ChatColor.GOLD + "You removed warning " + ChatColor.RED + warning + ChatColor.GOLD + " from " + ChatColor.RED + player);
            setWarnings(player, type, -1);
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

    private String getStartPath(String player, String type) {
        return warnings.getPath(player.toLowerCase(), "Warning", type);
    }
}