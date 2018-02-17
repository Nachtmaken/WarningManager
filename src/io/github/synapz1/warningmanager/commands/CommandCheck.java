package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Utils;
import io.github.synapz1.warningmanager.utils.WarningsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandCheck extends BaseCommand {

    public void onCommand(CommandSender sender, String[] args) {
        WarningsAPI.getWarningsAPI().check(sender, Bukkit.getOfflinePlayer(args[0]).getUniqueId());
    }

    public String getName() {
        return "check";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.check 1");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(1);
    }

    public String getArguments() {
        return "<player>";
    }

    public String getDescription() {
        return "Check a player's past warnings.";
    }

}
