package me.synapz.warnings.base;

import static org.bukkit.ChatColor.*;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class BaseCommand {

    public abstract void onCommand(CommandSender sender, String[] args);

    public abstract String getName();

    public abstract ArrayList<String> getPermissions();

    public abstract ArrayList<Integer> handledArgs();

    public abstract String getArguments();

    public abstract String getDescription();

    public String getCorrectUsage() {
        return RED + "Usage: /" + this.getName() + " " + this.getArguments();
    }

    public String getHelpInfo() {
        return GOLD + "/" + this.getName() + " " + this.getArguments() + DARK_GRAY + " - " + GRAY + this.getDescription();
    }
}