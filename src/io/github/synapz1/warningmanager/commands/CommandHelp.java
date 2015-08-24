package me.synapz.warnings.commands;

import me.synapz.warnings.utils.Utils;
import me.synapz.warnings.WarningManager;
import me.synapz.warnings.base.BaseCommand;
import org.bukkit.command.CommandSender;
import static org.bukkit.ChatColor.*;

import java.util.ArrayList;

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
        ArrayList<String> permissions = new ArrayList<>();
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
