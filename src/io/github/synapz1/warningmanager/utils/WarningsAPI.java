package io.github.synapz1.warningmanager.utils;


import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.utils.Messenger;
import io.github.synapz1.warningmanager.utils.Utils;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WarningsAPI {

    FileConfiguration file = SettingsManager.getManager().getWarningFile();
    private static WarningsAPI instance = new WarningsAPI();

    private WarningsAPI() {}

    public static WarningsAPI getWarningsAPI() {
        return instance;
    }

    public void addWarning(CommandSender sender, String p, String reason)
    {
        // Set the file values
        setWarnings(p, 1);
        setReason(p, reason);
        setSender(p, sender);

        // get the messages ready to be broadcasted by replacing tags with their values
        String broadcastMessage = SettingsManager.BROADCAST_MESSAGE.replace("%SENDER%", sender.getName());
        broadcastMessage = broadcastMessage.replace("%PLAYER%", p);
        broadcastMessage = broadcastMessage.replace("%REASON%", reason);

        // print output
        Messenger.getMessenger().broadcastMessage(broadcastMessage);

        String playerMessage = SettingsManager.PLAYER_MESSAGE;
        // quick check to make sure the PLAYER_MESSAGE is not 'none' because if it is we don't want to a send msg to player
        if (!isNone(playerMessage)) {
            playerMessage = playerMessage.replace("%SENDER%", sender.getName());
            playerMessage = playerMessage.replace("%REASON%", reason);
            playerMessage = playerMessage.replace("%WARNINGS%", getWarningsInt(p) + "");

            Utils.tryToSendPlayerMessage(playerMessage, p);
        }

        if (SettingsManager.getManager().getPunishments().contains(getWarningsInt(p))) {
            String punishment = SettingsManager.getManager().getPunishmentCommand(getWarningsInt(p));
            punishment = punishment.replace("%PLAYER%", p);
            punishment = punishment.replace("%SENDER%", sender.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishment);
        }

    }

    public void setWarnings(String p, int amount)
    {
        int newTotalWarnings = getWarningsInt(p) + amount;
        file.set(p.toLowerCase() + ".Total-Warnings", newTotalWarnings);
        SettingsManager.getManager().saveFiles();
    }

    public int getWarningsInt(String p)
    {
        return file.getInt(p.toLowerCase() + ".Total-Warnings");
    }

    public void setReason(String p, String reason)
    {
        file.set(p.toLowerCase() + ".Warning" + getWarningsInt(p) + ".Reason", reason);
        SettingsManager.getManager().saveFiles();
    }

    public String getReason(String p, int reasonNumber)
    {
        return file.getString(p.toLowerCase() + ".Warning" + reasonNumber + ".Reason");
    }

    public void setSender(String p, CommandSender sender)
    {
        file.set(p.toLowerCase() + ".Warning" + getWarningsInt(p) + ".Sender", sender.getName());
        SettingsManager.getManager().saveFiles();
    }

    public String getSender(String p, int reasonNumber)
    {
        return file.getString(p.toLowerCase() + ".Warning" + reasonNumber + ".Sender");
    }

    public void reset(String p)
    {
        file.set(p.toLowerCase(), null);
        SettingsManager.getManager().saveFiles();
    }

    public void check(CommandSender sender, String p)
    {
        int warningsAmount = getWarningsInt(p);

        sender.sendMessage(RED + "**********" + GOLD + p + RED + "**********");

        for(int i = 1; i <= warningsAmount+40; i++)
        {
            if (getSender(p, i) != null && getReason(p, i) != null) {
                sender.sendMessage(GOLD + "Warning #" + i + ": ");
                sender.sendMessage(GOLD + "  Reason: " + RED + getReason(p, i));
                sender.sendMessage(GOLD + "  Sender: " + RED + getSender(p, i));
            }
        }

        sender.sendMessage(GOLD + "Total Warnings: " + RED + getWarningsInt(p));
    }

    public void remove(CommandSender sender, String player, int warning) {
        if (getSender(player, warning) != null) {
            file.set(player.toLowerCase() + ".Warning" + warning, null);
            sender.sendMessage(GOLD + "You removed warning " + RED + warning + GOLD + " from " + RED + player);
            setWarnings(player, -1);
            SettingsManager.getManager().saveFiles();
        } else {
            sender.sendMessage(GOLD + "Player " + RED + player + GOLD + " does not have warning " + RED + warning);
        }

    }

    public void notifyOnReset(CommandSender sender, String target)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.isOp() || p.hasPermission("warnings.notify"))
            {
                if(!sender.getName().equals(p.getName()))
                {
                    p.sendMessage(GOLD + "Please be aware that " + RED + sender.getName() +
                            GOLD + " has reset " + RED + target + GOLD + "'s warnings.");
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
