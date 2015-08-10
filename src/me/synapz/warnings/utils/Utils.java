package me.synapz.warnings.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<String> allPermArguments(String permission) {
        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 1; i < 200; i++) {
            permissions.add(permission + " " + i);
        }
        return permissions;
    }

    public static ArrayList<Integer> allArguments() {
        ArrayList<Integer> args = new ArrayList<>();
        for (int i = 1; i < 200; i++) {
            args.add(i);
        }
        return args;
    }

    public static ArrayList<Integer> makeArgs(int...args) {
        ArrayList<Integer> arguments = new ArrayList<>();
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
            // player is offline, do nothing
        }
    }
}
