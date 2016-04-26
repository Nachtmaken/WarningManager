package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.utils.Utils;
import io.github.synapz1.warningmanager.utils.WarningsAPI;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CommandDelete extends TypeCommand {

    public void onCommand() {
        int warning;
        try {
            warning = Integer.parseInt(args[2]);
            WarningsAPI.getWarningsAPI().remove(sender, args[1], type, warning);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please use a valid number.");
            sender.sendMessage(this.getCorrectUsage());
        }
    }

    public String getName() {
        return "delete";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.delete 3");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(3);
    }

    public String getArguments() {
        return TYPE_LIST + " <player> <warningNumber>";
    }

    public String getDescription() {
        return "Deletes a warning from a player.";
    }

}
