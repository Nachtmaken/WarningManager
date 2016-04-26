package io.github.synapz1.warningmanager.commands;


import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.base.BaseCommand;
import io.github.synapz1.warningmanager.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public abstract class TypeCommand extends BaseCommand {

    protected String TYPE_LIST = getTypeList();

    protected CommandSender sender;
    protected String[] args;
    protected String type;

    public void onCommand(CommandSender sender, String[] args) {
        this.args = args;
        this.type = args[0];
        this.sender = sender;

        if (!isType(type)) {
            Messenger.getMessenger().message(sender, ChatColor.RED + "Please enter a valid group. Choose " + TYPE_LIST);
            return;
        }

        onCommand();
    }

    public abstract void onCommand();

    public abstract String getName();

    public abstract ArrayList<String> getPermissions();

    public abstract ArrayList<Integer> handledArgs();

    public abstract String getArguments();

    public abstract String getDescription();

    private boolean isType(String strType) {
        for (String type : SettingsManager.getManager().getPunishments().keySet()) {
            if (type.equalsIgnoreCase(strType))
                return true;
        }
        return false;
    }

    private String getTypeList() {
        // Generates the type list. Turns groups in config.yml into <hack/grief/advertise>
        String types = "<";

        for (String type : SettingsManager.getManager().getPunishments().keySet())
            types += type + "/";

        types = types.substring(0, types.lastIndexOf("/"));
        types += ">";

        return types;
    }
}
