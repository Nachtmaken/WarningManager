package me.synapz.warnings;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WarningsAPI {

    ChatColor gold = ChatColor.GOLD;
    ChatColor red = ChatColor.RED;
    ChatColor white = ChatColor.WHITE;
    ChatColor gray = ChatColor.GRAY;
    FileConfiguration file = SettingsManager.getManager().getWarningFile();
    
    protected void addWarning(CommandSender sender, String p, String reason)
    {

        if(reason.equals(""))
        {
            reason = SettingsManager.DEFAULT_REASON;
        }

        // Set the file values
        setWarnings(p, 1);
        setReason(p, reason);
        setSender(p, sender);

        // get the messages ready to be broadcasted by replacing tags with their values
        String broadcastMessage = SettingsManager.BROADCAST_MESSAGE.replace("%SENDER%", sender.getName());
        broadcastMessage = broadcastMessage.replace("%PLAYER%", p);
        broadcastMessage = broadcastMessage.replace("%REASON%", reason);

        // todo: fix bug here, reason isnt displayed
        String playerMessage = SettingsManager.PLAYER_MESSAGE;
        playerMessage = playerMessage.replace("%SENDER%", sender.getName());
        playerMessage = playerMessage.replace("%REASON%", reason);
        
        // print outputs
        Messenger.getMessenger().broadcastMessage(broadcastMessage);
        tryToSendPlayerMessage(playerMessage, p);
    }

    protected void setWarnings(String p, int amount)
    {
        int newTotalWarnings = getWarningsInt(p) + amount;
        file.set(p + ".Total-Warnings", newTotalWarnings);
        SettingsManager.getManager().saveFiles();
    }

    protected int getWarningsInt(String p)
    {
        return file.getInt(p + ".Total-Warnings");
    }

    protected void resetWarnings(String p)
    {
        file.set(p + ".Total-Warnings", 0);
    }

    protected void setReason(String p, String reason)
    {
        file.set(p + ".Warning" + getWarningsInt(p) + ".Reason", reason);
        SettingsManager.getManager().saveFiles();
    }

    protected String getReason(String p, int reasonNumber)
    {
        return file.getString(p + ".Warning" + reasonNumber + ".Reason");
    }

    protected void setSender(String p, CommandSender sender)
    {


        file.set(p + ".Warning" + getWarningsInt(p) + ".Sender", sender.getName());
        SettingsManager.getManager().saveFiles();
    }

    protected String getSender(String p, int reasonNumber)
    {
        return file.getString(p + ".Warning" + reasonNumber + ".Sender");
    }

    protected void reset(String p)
    {
        file.set(p, null);
        SettingsManager.getManager().saveFiles();
    }

    protected String produceReason(String[] args)
    {
        String reason = "";
        for(int i = 2; i < args.length; i++){

            // this also removes the " " on the last argument so it isn't "{WARNING} "
            reason = i+1 == args.length ? reason + args[i] : reason + args[i] + " ";

        }
        return reason;
    }

    protected void check(CommandSender sender, String p)
    {

        int warningsAmount = getWarningsInt(p);

        sender.sendMessage(red + "**********" + gold + p + red + "**********");

        for(int i = 1; i <= warningsAmount; i++)
        {
            sender.sendMessage(gold + "Warning #" + i + ": ");
            sender.sendMessage(gold + "  Reason: " + red + getReason(p, i));
            sender.sendMessage(gold + "  Sender: " + red + getSender(p, i));
        }

        sender.sendMessage(gold + "Total Warnings: " + red + getWarningsInt(p));
    }


    protected boolean checkPermissions(CommandSender sender, String permission)
    {
        if(sender.hasPermission(permission))
        {
            return true;
        }
        else
        {
            sender.sendMessage(red + "You don't have access to that command!");
            return false;
        }
    }

    protected void notifyOnReset(CommandSender sender, String target)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.isOp() || p.hasPermission("warnings.notify"))
            {
                if(!sender.equals(p.getName()))
                {
                    p.sendMessage(ChatColor.GOLD + "Please be aware that " + red + sender.getName() +
                            gold + " has reset " + red + target + gold + "'s warnings.");
                }
            }
        }
    }

    protected void tryToSendPlayerMessage(String message, String p)
    {
        try{
            Player player = Bukkit.getServer().getPlayer(p);
            Messenger.getMessenger().message(player, message);
        }catch (NullPointerException e)
        {
            // player is offline, do nothing
        }
    }


    protected void wrongUsage(CommandSender player)
    {
        player.sendMessage(white + "•*•*•*•*•*•*•*• " + gray + "WarningManager Command Menu" + white + " •*•*•*•*•*•*•*•");
        player.sendMessage("");
        player.sendMessage(gold + "/warnings" + white +       " • " + gray + "Display the command menu.");
        player.sendMessage(gold + "/warnings check <player>" + white + " • " + gray + "Check a players past warnings.");
        player.sendMessage(gold + "/warnings reset <player>" + white + " • " + gray + "Reset a player's warnings");
        player.sendMessage(gold + "/warnings warn <player> [reason]" + white + " • " + gray + "Warn a player.");
        player.sendMessage(gold + "/warnings reload" + white + " • " + gray + "Reload config.yml");
    }

}
