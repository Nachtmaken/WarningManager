package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.WarningManager;
import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Messenger;
import io.github.synapz1.warningmanager.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandReload extends BaseCommand {

    private WarningManager wm;

    public CommandReload(WarningManager wm) {
        this.wm = wm;
    }

    public void onCommand(CommandSender sender, String[] args) {
        wm.reloadConfig();
        SettingsManager.getManager().loadValues(wm.getConfig());
        Messenger.getMessenger().message(sender, ChatColor.GOLD + "All settings were reloaded!");
    }

    public String getName() {
        return "wreload";
    }

    public ArrayList<String> getPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();
        permissions.add("warnings.reload 0");
        return permissions;
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.makeArgs(0);
    }

    public String getArguments() {
        return "";
    }

    public String getDescription() {
        return "Reload plugin configuration files.";
    }

}
