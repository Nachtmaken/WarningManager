package me.synapz.warnings;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {

	private Messenger() {}
	
	private static Messenger instance = new Messenger();
	
	public static Messenger getMessenger() {
		return instance;
	}
	
	public void message(CommandSender sender, String... message) {
		for (String msg : message) {
			sender.sendMessage(SettingsManager.PREFIX + msg);
		}
	}
	
	public void broadcastMessage(String message) {
		String prefix = SettingsManager.PREFIX;
		if (SettingsManager.broadcast) {
			Bukkit.broadcastMessage(prefix + message);
		} else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("warnings.notify")) {
                    Bukkit.broadcastMessage(prefix + message);
                }
            }
		}
	}
	

}
