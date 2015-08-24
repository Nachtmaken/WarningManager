package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Utils;
import io.github.synapz1.warningmanager.utils.WarningsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandRemove extends BaseCommand {

    public void onCommand(CommandSender sender, String[] args) {
        int warning;
        try {
            warning = Integer.parseInt(args[1]);
            WarningsAPI.getWarningsAPI().remove(sender, args[0], warning);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please use a valid number.");
            sender.sendMessage(this.getCorrectUsage());
        }

    }

    public String getName() {
        return "remove";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.remove 2");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(2);
    }

    public String getArguments() {
        return "<player> <warningNumber>";
    }

    public String getDescription() {
        return "Removes a warning from a player.";
    }

}
