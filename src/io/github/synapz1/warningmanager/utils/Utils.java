package io.github.synapz1.warningmanager.utils;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.WarningManager;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static ArrayList<String> allPermArguments(String permission) {
        ArrayList<String> permissions = new ArrayList<String>();
        for (int i = 1; i < 200; i++) {
            permissions.add(permission + " " + i);
        }
        return permissions;
    }

    public static ArrayList<Integer> allArguments() {
        ArrayList<Integer> args = new ArrayList<Integer>();
        for (int i = 1; i < 200; i++) {
            args.add(i);
        }
        return args;
    }

    public static ArrayList<Integer> makeArgs(int...args) {
        ArrayList<Integer> arguments = new ArrayList<Integer>();
        for (Integer arg : args) {
            arguments.add(arg);
        }
        return arguments;
    }

    public static String produceReason(String[] args)
    {
        String reason = "";
        for(int i = 2; i < args.length; i++){

            // this also removes the " " on the last argument so it isn't "{WARNING} "
            reason = i+1 == args.length ? reason + args[i] : reason + args[i] + " ";

        }
        return reason;
    }

    public static void tryToSendPlayerMessage(String message, UUID p)
    {
        Player player = Bukkit.getServer().getPlayer(p);
        if (player != null) {
            Messenger.getMessenger().message(player, message);
        } else {
            FileConfiguration config = SettingsManager.getManager().getOfflineWarningsFile().getFileConfig();
            config.set("Players." + p + ".Messages", message);
            SettingsManager.getManager().getOfflineWarningsFile().saveFile();
        }
    }

    public static String makeSpaces(int spaces) {
        String strSpace = "";

        while (spaces > 0) {
            strSpace += " ";
            spaces--;
        }
        return strSpace;
    }
}
