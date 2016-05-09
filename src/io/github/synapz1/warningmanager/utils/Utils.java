package io.github.synapz1.warningmanager.utils;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.WarningManager;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

    public static void tryToSendPlayerMessage(String message, String p)
    {
        try{
            Player player = Bukkit.getServer().getPlayer(p);
            Messenger.getMessenger().message(player, message);
        }catch (NullPointerException e)
        {
            List<String> messages = new ArrayList<String>();
            FileConfiguration config = SettingsManager.getManager().getOfflineWarningsFile().getFileConfig();
            if (config.contains("Players." + p + ".Messages")) messages.addAll(config.getStringList("Players." + p + ".Messages"));
            messages.add(message);
            config.set("Players." + p + ".Messages", messages);
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
