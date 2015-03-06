package me.synapz.warnings;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class WarningManager extends JavaPlugin implements CommandExecutor{

    ChatColor red = ChatColor.RED;
    ChatColor gold = ChatColor.GOLD;
    ChatColor white = ChatColor.WHITE;
    ChatColor gray = ChatColor.GRAY;

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        saveConfig();

    }

    @Override
    public void onDisable()
    {
        this.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        WarningsAPI api = new WarningsAPI(this);
        if(command.getName().equalsIgnoreCase("warnings"))
        {
            if(args.length >= 2)
            {
                String target = args[1];
                if(args[0].equalsIgnoreCase("warn"))
                {
                    if(api.checkPermissions(sender, "warnings.warn"))
                    {
                        String reason = api.produceReason(args);

                        api.addWarning(sender, target, reason, api);

                    }
                }
                else if(args[0].equalsIgnoreCase("check"))
                {
                    if(api.checkPermissions(sender, "warnings.check"))
                    {
                        api.check(sender, target);
                    }
                }
                else if(args[0].equalsIgnoreCase("reset"))
                {
                        if(api.checkPermissions(sender, "warnings.reset"))
                        {

                            api.reset(target);

                            api.notifyOnReset(sender, target);

                            sender.sendMessage(gold + "You have reset " + red + target + gold + "'s warnings.");
                            api.tryToSendPlayerMessage(gold + "Your warnings were reset!", target);
                        }
                    }
                }
                else if(args.length == 0 || args.length == 1 || !args[0].equals("warn") || !args[0].equalsIgnoreCase("check") || !args[0].equalsIgnoreCase("reset"))
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


