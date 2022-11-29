package me.bomb.tokencommands;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

abstract class CommandManager {
	
	protected static final CommandManager commandmanager;
	protected static Object outchatpacket;
	
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":
			commandmanager = new CommandManager_v1_16_R3();
			break;
		case "v1_15_R1":
			commandmanager = new CommandManager_v1_15_R1();
			break;
		case "v1_14_R1":
			commandmanager = new CommandManager_v1_14_R1();
			break;
		case "v1_13_R2":
			commandmanager = new CommandManager_v1_13_R2();
			break;
		case "v1_12_R1":
			commandmanager = new CommandManager_v1_12_R1();
			break;
		case "v1_11_R1":
			commandmanager = new CommandManager_v1_11_R1();
			break;
		case "v1_10_R1":
			commandmanager = new CommandManager_v1_10_R1();
			break;
		case "v1_9_R2":
			commandmanager = new CommandManager_v1_9_R2();
			break;
		case "v1_8_R3":
			commandmanager = new CommandManager_v1_8_R3();
			break;
		case "v1_7_R4":
			commandmanager = new CommandManager_v1_7_R4();
			break;
		default:
			commandmanager = null;
		}
	}
	
	protected static final void start(Player player) {
		commandmanager.register(player);
	}
	
	protected static final void end(Player player) {
		commandmanager.unregister(player);
		TokenCommand.removeLogout(player.getUniqueId());
		MessageCache.removeLogout(player.getUniqueId());
	}
	
	protected static final void enable(Collection<? extends Player> collection) {
		CommandExecutor.initRunnable();
		collection.forEach(player -> commandmanager.register(player));
	}
	protected static final void disable(Collection<? extends Player> players) {
		players.forEach(player -> commandmanager.unregister(player));
		CommandExecutor.endTask();
	}
	
	protected final static boolean read(Player player,String command) {
		if(command.startsWith("/////")&&command.length() > 41&&command.length()<46) {
			
			UUID token = null;
			short cmdnum = -1;
			try {
				token = UUID.fromString(command.substring(5,41));
				cmdnum = (short)Integer.parseInt("0000".concat(command.substring(41,command.length())), 16);
				//Bukkit.getLogger().info(player.getName() + " issued server token command: " + token + " № " + cmdnum);
			} catch (NumberFormatException e) {
				return false;
			} catch (IllegalArgumentException e) {
				return false;
			}
			if(token==null||cmdnum==-1) {
				return false;
			}
			TokenCommand.execute(player, token, cmdnum);
			return true;
		}
		return false;
	}

	protected abstract void register(Player player);
	protected abstract void resend(Collection<Object> cachedmessages,Object context);
	protected abstract void unregister(Player player);
}
