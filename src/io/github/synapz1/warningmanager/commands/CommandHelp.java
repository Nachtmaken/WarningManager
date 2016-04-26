package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.WarningManager;
import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

import static org.bukkit.ChatColor.*;

public class CommandHelp extends BaseCommand {

    private WarningManager wm;

    public CommandHelp(WarningManager wm) {
        this.wm = wm;
    }

    public void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(GRAY + ""+ STRIKETHROUGH + BOLD + "----------" + DARK_GRAY + BOLD + STRIKETHROUGH + "[-" + RESET + GOLD + BOLD + " Warning Manager " + DARK_GRAY + BOLD + STRIKETHROUGH + "-]" + GRAY + BOLD + STRIKETHROUGH + "----------" );
        for (BaseCommand cmd : wm.getCommands()) {
            sender.sendMessage(cmd.getHelpInfo());
        }
    }

    public String getName() {
        return "whelp";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.help 0");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(0);
    }

    public String getArguments() {
        return "";
    }

    public String getDescription() {
        return "Display WarningsManager command menu.";
    }

}
