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


    protected void addWarning(CommandSender sender, String p, String racist, String reason)
    {

        if(reason.equals(""))
        {
            reason = DEFAULT_REASON;
        }

        // if it is 't' its red, if its 'f' its gold
        String suffixToAdd = racist.equals("t") ? "&4I" : "&6I";

        // add the old + new suffix together + check if the suffix is even there
        String currentSuffix = getSuffix(p, getWarningsInt(p));

        // set the config
        setBans(p, 0);
        setWarnings(p, 1);
        setSuffix(p, currentSuffix, suffixToAdd);
        setReason(p, reason);
        setSender(p, sender);
        setRacist(p, racist);


        // print outputs
        Bukkit.broadcastMessage(red + p + gold + " was warned by " + red + sender.getName() + gold + " for " + reason);
        tryToSendPlayerMessage(gold + "You were warned. You have " + red + getWarningsInt(p) + gold + " warnings", p);

        // do commands + check to ban player if its == to 3 or 6 etc.
        wm.getServer().dispatchCommand(wm.getServer().getConsoleSender(), "manuaddv " + p + " suffix ' " + getSuffix(p, getWarningsInt(p)) + "'");

        if(getWarningsInt(p) % 3 == 0)
        {
            // add 1 to the bans int
            setBans(p, 1);
            resetWarnings(p);
            resetSuffix(p);
            wm.getServer().dispatchCommand(wm.getServer().getConsoleSender(), "itemp " + p + " 3d You have received to many warnings!");
            wm.getServer().dispatchCommand(wm.getServer().getConsoleSender(), "manudelv " + p + " suffix ' '");
        }


        // add suffix




    }

    protected String getSuffix(String p, int warningNumber)
    {
        String currentSuffix = config.getString(p + ".Suffix");

        if(currentSuffix == null)
        {
            currentSuffix = "";
        }

        System.out.print(currentSuffix);

        return currentSuffix;

    }

    protected void setSuffix(String p, String beforeSuffix, String suffixToAdd)
    {
        config.set(p + ".Suffix", beforeSuffix + suffixToAdd);
        wm.saveConfig();
    }

    protected void resetSuffix(String p)
    {
        config.set(p + ".Suffix", "");
    }

    protected void setRacist(String p, String tf)
    {
        String racism = tf.equals("t") ? "true" : "false";

        config.set(p + ".Warning" + getWarningsInt(p) + ".Racist", racism);
    }

    protected String getRacism(String p, int warningNumber)
    {
        return config.getString(p + ".Warning" + warningNumber + ".Racist");
    }

    protected void setWarnings(String p, int amount)
    {

        int newTotalWarnings = getWarningsInt(p) + amount;
        config.set(p + ".Total-Warnings", newTotalWarnings);
        wm.saveConfig();
    }

    protected int getWarningsInt(String p)
    {
        return config.getInt(p + ".Total-Warnings");
    }

    protected void resetWarnings(String p)
    {
        config.set(p + ".Total-Warnings", 0);
    }

    protected int getBans(String p)
    {
        return config.getInt(p + ".Total-Bans");
    }


    protected void setBans(String p, int amount)
    {
        int bans = config.getInt(p + ".Total-Bans");
        config.set(p + ".Total-Bans", bans + amount);
        wm.saveConfig();
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
        wm.getServer().dispatchCommand(wm.getServer().getConsoleSender(), "/manudelv " + p + " suffix ' '");
        wm.saveConfig();
    }

    protected String produceReason(String[] args)
    {
        String reason = "";
        for(int i = 3; i < args.length; i++){

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
            sender.sendMessage(gold + "  Racist: " + red + getRacism(p, i));
            sender.sendMessage(gold + "  Suffix: " + red + getSuffix(p, i));
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
                    p.sendMessage(ChatColor.GOLD + "Please be awhere that " + red + sender.getName() +
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
        player.sendMessage(gold + "/warnings check <player>" + white + " • " + gray + "Check a players past warnings.");
        player.sendMessage(gold + "/warnings reset <player>" + white + " • " + gray + "Reset a player's warnings");
        player.sendMessage(gold + "/warnings warn <t/f> <player> [reason]" + white + " • " + gray + "Warn a player.");
        player.sendMessage(gold + "* - Warning for racism? 't' means true 'f' means false.");
        player.sendMessage(gold + "Author: " + gray + "Synapz_");

    }

}
