package me.bomb.tokencommands;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenCommands extends JavaPlugin implements Listener {
	
	private final static boolean supported;
	
	protected static TokenCommands plugin;
	
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":case "v1_15_R1":case "v1_14_R1":case "v1_13_R2":case "v1_12_R1":case "v1_11_R1":case "v1_10_R1":case "v1_9_R2":case "v1_8_R3":case "v1_7_R4": 
			supported = true;
		break;
		default: 
			supported = false;
			break;
		}
	}
	
	public void onEnable() {
		if(supported) {
			plugin = this;
		    Bukkit.getPluginManager().registerEvents(this, this);
		    OnlinePlayers.onlineplenable();
			return;
		}
		getLogger().log(Level.WARNING, "Unsupported version!");
		getLogger().log(Level.WARNING, "Supported versions: 1.7.10,1.8.4(+4),1.9.4,1.10(+2),1.11(+2),1.12(+2),1.13.2,1.14(+3),1.15(+2),1.16.4(+1)");
		getServer().getPluginManager().disablePlugin(this);
	}
	public void onDisable() {
	    if(supported) {
	    	OnlinePlayers.onlinepldisable();
	    }
	}
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		CommandManager.start(event.getPlayer());
	}
	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		CommandManager.end(event.getPlayer());
	}
}
