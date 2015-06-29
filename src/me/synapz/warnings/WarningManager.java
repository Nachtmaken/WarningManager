package me.synapz.warnings;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class WarningManager extends JavaPlugin implements CommandExecutor{

    ChatColor red = ChatColor.RED;
    ChatColor gold = ChatColor.GOLD;
    
    @Override
    public void onEnable()
    {
        SettingsManager.getManager().init(this);

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException localIOException) {}

    }

    @Override
    public void onDisable()
    {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WarningsAPI api = new WarningsAPI();
        if(command.getName().equalsIgnoreCase("warnings"))
        {
            if(args.length >= 2)
            {
                if(args[0].equalsIgnoreCase("warn"))
                {
                    String target = args[1];
                    
                    if(api.checkPermissions(sender, "warnings.warn"))
                    {
                        String reason = api.produceReason(args);

                        api.addWarning(sender, target, reason);
                        return false;
                    }
                }
                else if(args[0].equalsIgnoreCase("check"))
                {
                    String target = args[1];
                    if(api.checkPermissions(sender, "warnings.check"))
                    {
                        api.check(sender, target);
                    }
                }
                else if(args[0].equalsIgnoreCase("reset"))
                {
                    String target = args[1];
                    if(api.checkPermissions(sender, "warnings.reset"))
                    {
                        api.reset(target);
                        api.notifyOnReset(sender, target);

                        sender.sendMessage(gold + "You have reset " + red + target + gold + "'s warnings.");
                        api.tryToSendPlayerMessage(gold + "Your warnings were reset!", target);
                    }
                }
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) 
            {
            	if (api.checkPermissions(sender, "warnings.reload"))
            	{
                    this.reloadConfig();
                	SettingsManager.getManager().loadValues(this.getConfig());
                	Messenger.getMessenger().message(sender, ChatColor.GOLD + "All settings were reloaded!");
            	}
            }
            else
            {
                if(api.checkPermissions(sender, "warnings.help"))
                {
                    api.wrongUsage(sender);
                }
            }
        }
    return false;
    }
}

