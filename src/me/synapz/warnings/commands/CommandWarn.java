package me.synapz.warnings.commands;

import me.synapz.warnings.base.BaseCommand;
import me.synapz.warnings.SettingsManager;
import me.synapz.warnings.utils.Utils;
import me.synapz.warnings.utils.WarningsAPI;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandWarn extends BaseCommand {

    public void onCommand(CommandSender sender, String[] args) {
        String reason = SettingsManager.DEFAULT_REASON;
        String target = args[0];

        if (args.length >= 2) {
            reason = Utils.produceReason(args);
        }
        WarningsAPI.getWarningsAPI().addWarning(sender, target, reason);
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
        return "<player> [reason]";
    }

    public String getDescription() {
        return "Warn a player.";
    }
}
