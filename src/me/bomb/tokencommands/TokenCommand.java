package me.bomb.tokencommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

final class TokenCommand {
	
	private static HashMap<UUID,HashSet<TokenCommand>> playertokenmessages = new HashMap<UUID,HashSet<TokenCommand>>();
	private final UUID token;
	private final ArrayList<ArrayList<String>> msgcmds;
	private byte usecount;
	private final BukkitTask task;
	//maximum use counts = 255 or infinity if 0
	//maximum time = 4 minutes 15 seconds or infinity if 0
	private TokenCommand(UUID playeruuid,ArrayList<ArrayList<String>> msgcmds,byte usecount,byte time) {
		this.token = UUID.randomUUID();
		this.msgcmds = msgcmds;
		this.usecount = usecount;
		if(time!=0) {
			TokenCommand thistokencommand = this;
			task = new BukkitRunnable() {
				@Override
				public void run() {
					if(playertokenmessages.containsKey(playeruuid) && playertokenmessages.get(playeruuid).removeIf(tokencommand -> tokencommand == thistokencommand)) {
						MessageCache.removeHolddown(playeruuid, token);
					}
				}
			}.runTaskLaterAsynchronously(TokenCommands.plugin,(0x000000FF & time) * 20);
		} else {
			task = null;
		}
		HashSet<TokenCommand> tokencommands = playertokenmessages.containsKey(playeruuid) ? playertokenmessages.get(playeruuid) : new HashSet<TokenCommand>();
		tokencommands.add(this);
		playertokenmessages.put(playeruuid, tokencommands);
		//Bukkit.getLogger().info("Registred " + msgcmds.size() + " commands with token " + token);
	}
	
	protected static UUID put(UUID playeruuid,ArrayList<ArrayList<String>> msgcmds,byte usecount,byte time) {
		return new TokenCommand(playeruuid,msgcmds,usecount,time).token;
	}
	
	protected static void execute(Player player,UUID token,short cmdnum) {
		UUID playeruuid = player.getUniqueId();
		if(!playertokenmessages.containsKey(playeruuid)) {
			return;
		}
		//boolean remove = playertokenmessages.get(playeruuid).removeIf(tokencommand -> tokencommand.token.equals(token) && cmdnum>-1 && tokencommand.msgcmds.size()>cmdnum && (tokencommand.msgcmds.get(cmdnum).removeIf(command -> command.equals("/////") || !CommandExecutor.execute(player,command)) || tokencommand.usecount != 0 && --tokencommand.usecount == 0));
		playertokenmessages.get(playeruuid).removeIf(tokencommand -> {
			boolean remove = tokencommand.token.equals(token) && cmdnum>-1 && tokencommand.msgcmds.size()>cmdnum && (tokencommand.msgcmds.get(cmdnum).removeIf(command -> command.equals("/////") || !CommandExecutor.execute(player,command)) || tokencommand.usecount != 0 && --tokencommand.usecount == 0);
			if(remove) {
				if(tokencommand.task!=null) {
					tokencommand.task.cancel();
				}
				MessageCache.removeHolddown(playeruuid, token);
			}
			return remove;
		});
		return;
	}
	
	protected static void removeLogout(UUID playeruuid) {
		playertokenmessages.remove(playeruuid).forEach(tokencommand -> {
			if(tokencommand.task !=null) {
				tokencommand.task.cancel();
			}
		});
	}
	
	
	
}
