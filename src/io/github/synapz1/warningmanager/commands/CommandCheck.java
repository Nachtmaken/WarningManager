package me.synapz.warnings.commands;

import me.synapz.warnings.utils.Utils;
import me.synapz.warnings.utils.WarningsAPI;
import me.synapz.warnings.base.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandCheck extends BaseCommand {

    public void onCommand(CommandSender sender, String[] args) {
        WarningsAPI.getWarningsAPI().check(sender, args[0]);
    }

    public String getName() {
        return "check";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
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
