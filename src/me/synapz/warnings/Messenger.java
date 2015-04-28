package me.synapz.warnings;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Messenger {
	
	
	private static final String PREFIX = " ";
	
	private Messenger() {}
	
	private static Messenger instance = new Messenger();
	
	public static Messenger getMessenger() {
		return instance;
	}
	
	public void message(CommandSender sender, String... message) {
		for (String msg : message) {
			sender.sendMessage(PREFIX + msg);
		}
	}
	
	public void broadcastMessage(String message) {
		String prefix = SettingsManager.PREFIX;
		if (SettingsManager.broadcast) {
			Bukkit.broadcastMessage(prefix + message);
		} else {				
			Bukkit.broadcast(prefix + message, "warnings.notify");
		}
	}
	

}
