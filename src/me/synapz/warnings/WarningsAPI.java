package me.synapz.warnings;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class WarningsAPI {

    ChatColor gold = ChatColor.GOLD;
    ChatColor red = ChatColor.RED;
    ChatColor white = ChatColor.WHITE;
    ChatColor gray = ChatColor.GRAY;
    WarningManager wm;
    FileConfiguration config;
    public static final String DEFAULT_REASON = "Follow all rules.";


    public WarningsAPI(WarningManager manager)
    {
       wm = manager;
        config = manager.getConfig();
    }


    protected void addWarning(CommandSender sender, String p, String reason, WarningsAPI wm)
    {

        if(reason.equals(""))
        {
            reason = DEFAULT_REASON;
        }

        int warnings = wm.getWarningsInt(p);

        wm.setWarnings(p, 1);
        wm.setReason(p, reason);
        wm.setSender(p, sender);

        Bukkit.broadcastMessage(red + p + gold + " was warned by " + red + sender.getName() + gold + " for " + reason);

        tryToSendPlayerMessage(gold + "You were warned. You have " + red + getWarningsInt(p) + gold + " warnings", p);

        if(warnings >= 3)
        {
            wm.addBan(p);
            // ban player
        }


    }


    protected void setWarnings(String p, int amount)
    {

        int newTotalWarnings = getWarningsInt(p) + amount;
        config.set(p + ".Total-Warnings", newTotalWarnings);
        wm.saveConfig();
    }



    protected int getBans(String p)
    {
        return config.getInt(p + ".Total-Bans");
    }


    protected void addBan(String p)
    {
        int bans = getBans(p);
        config.set(p + ".Total-Bans", bans++);
    }


    protected int getWarningsInt(String p)
    {
        return config.getInt(p + ".Total-Warnings");
    }


    protected void setReason(String p, String reason)
    {
        config.set(p + ".Warning" + getWarningsInt(p) + ".Reason", reason);
        wm.saveConfig();
    }

    protected String getReason(String p, int reasonNumber)
    {
        return config.getString(p + ".Warning" + reasonNumber + ".Reason");
    }



    protected void setSender(String p, CommandSender sender)
    {


        config.set(p + ".Warning" + getWarningsInt(p) + ".Sender", sender.getName());
        wm.saveConfig();
    }


    protected String getSender(String p, int reasonNumber)
    {

        return config.getString(p + ".Warning" + reasonNumber + ".Sender");
    }



    protected void reset(String p)
    {

        config.set(p, null);
        wm.saveConfig();
    }


    /*
     * calculate the reason from arguments given
     *
     * Example:
     *
     * Turn, /warnings warn <Player> You broke many rules
     *
     * Warning1:
     *   Reason: You broke many rules
    */

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
        sender.sendMessage(gold + "Total Bans: " + red + getBans(p));

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
                    p.sendMessage(ChatColor.GOLD + "Please be where that " + red + sender.getName() +
                            gold + " has reset " + red + target + gold + "'s warnings.");
                }
            }
        }
    }

    protected void tryToSendPlayerMessage(String message, String p)
    {
        try{
            Player player = Bukkit.getServer().getPlayer(p);
            player.sendMessage(message);
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
        player.sendMessage(gold+ "/warnings check <player>" + white + " • " + gray + "Check a players past warnings.");
        player.sendMessage(gold + "/warnings reset <player>" + white + " • " + gray + "Reset a player's warnings");
        player.sendMessage(gold + "/warnings warn <player> [reason]" + white + " • " + gray + "Warn a player.");
        player.sendMessage(gold + "Author: " + gray + "Synapz_");

    }

}
