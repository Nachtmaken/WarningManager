package io.github.synapz1.warningmanager.commands;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.utils.Messenger;
import io.github.synapz1.warningmanager.utils.Utils;
import io.github.synapz1.warningmanager.utils.WarningsAPI;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class CommandWarn extends TypeCommand {

    public void onCommand() {
        String reason = SettingsManager.DEFAULT_REASON.replace("%punishment%", type);

        if (args.length < 2) {
            Messenger.getMessenger().message(sender, ChatColor.RED + "Please specify a player.");
            return;
        }

        String target = args[1];

        if (args.length > 2) {
            reason = Utils.produceReason(args);
        }

        WarningsAPI.getWarningsAPI().addWarning(sender, target, reason, type.toLowerCase());
    }

    public String getName() {
        return "warn";
    }

    public ArrayList<String> getPermissions() {
        return Utils.allPermArguments("warnings.warn");
    }

    public ArrayList<Integer> handledArgs() {
        return Utils.allArguments();
    }

    public String getArguments() {
        return TYPE_LIST + " <player> [reason]";
    }

    public String getDescription() {
        return "Warn a player.";
    }
}