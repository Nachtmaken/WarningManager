package io.github.synapz1.warningmanager.commands;


import static org.bukkit.ChatColor.*;

import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Utils;
import io.github.synapz1.warningmanager.utils.WarningsAPI;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandReset extends BaseCommand {

    public void onCommand(CommandSender sender, String[] args) {
        WarningsAPI api = WarningsAPI.getWarningsAPI();
        api.reset(args[0]);
        api.notifyOnReset(sender, args[0]);

        if (!args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(GOLD + "You have reset " + RED + args[0] + GOLD + "'s warnings.");
        }
        Utils.tryToSendPlayerMessage(GOLD + "Your warnings were reset!", args[0]);
    }

    public String getName() {
        return "reset";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.reset 1");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(1);
    }

    public String getArguments() {
        return "<player>";
    }

    public String getDescription() {
        return "Reset a player's warnings.";
    }
}