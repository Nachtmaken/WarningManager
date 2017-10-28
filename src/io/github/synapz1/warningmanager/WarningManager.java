package io.github.synapz1.warningmanager;

import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.commands.*;
import io.github.synapz1.warningmanager.listeners.PlayerListener;
import io.github.synapz1.warningmanager.utils.Messenger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.RED;

public class WarningManager extends JavaPlugin implements CommandExecutor {

    private static ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();

    @Override
    public void onEnable() {
        SettingsManager.getManager().init(this);
        init();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException localIOException) {}

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isHandledCommand = false;
        BaseCommand cmd = null;

        for (BaseCommand baseCommand : commands) {
            if (command.getName().equalsIgnoreCase(baseCommand.getName())) {
                isHandledCommand = true;
                cmd = baseCommand;
            }
        }

        if (isHandledCommand) {
            try {
                if (isCorrectArgs(sender, cmd, args.length)) {
                    if (!(sender instanceof Player) || hasPerm(sender, args.length, cmd)) {
                        cmd.onCommand(sender, args);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            } catch (Exception e) {
                Messenger.getMessenger().message(sender, RED + "An unexpected error occurred. Check console log for more information.");
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    private boolean hasPerm(CommandSender sender, int argument, BaseCommand command) {
        String permission = "";
        for (String perm : command.getPermissions()) {
            String[] permList = perm.split(" ");
            if (Integer.parseInt(permList[1]) == argument) {
                permission = permList[0];
            }
        }
        if (sender.hasPermission(permission)) {
            return true;
        } else {
            sender.sendMessage(DARK_RED + "You don't have access to that command!");
            return false;
        }
    }

    private boolean isCorrectArgs(CommandSender sender, BaseCommand command, int argCount) {
        boolean correctArgCount = false;

        if (command.handledArgs().contains(argCount)) {
            correctArgCount = true;
        }

        if (!correctArgCount) {
            sender.sendMessage(RED + "Please review your argument count.");
            sender.sendMessage(RED + command.getCorrectUsage());
        }
        return correctArgCount;
    }

    private void addCommand(BaseCommand...cmd) {
        commands.addAll(Arrays.asList(cmd));
    }

    private void init() {
        addCommand(new CommandWarn(), new CommandCheck(), new CommandReset(), new CommandDelete(), new CommandReload(this), new CommandHelp(this));
    }

    public ArrayList<BaseCommand> getCommands() {
        return commands;
    }
}

