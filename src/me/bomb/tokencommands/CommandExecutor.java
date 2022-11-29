package me.bomb.tokencommands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

final class CommandExecutor {
	
	private static BukkitTask task = null;
	private static HashMap<Player,ArrayList<String>> toexecuteascommands = new HashMap<Player,ArrayList<String>>();
	
	protected static final void initRunnable() {
		task = new BukkitRunnable(){
		    @Override
		    public void run(){
		    	toexecuteascommands.keySet().forEach(player -> toexecuteascommands.get(player).forEach(command -> {
		    		if(command.startsWith("§§")) { //Run as console
		    			command = command.substring(2);
		    			command = command.replaceAll("§playername", player.getName());
		    			command = command.replaceAll("§playeruuid", player.getUniqueId().toString());
		    			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		    			//Bukkit.getLogger().info(player.getName().concat(" issued token command as console: ".concat(command)));
		    			return;
		    		}
		    		if(command.startsWith("§")) { //Run as op
		    			command = command.substring(1);
		    			if(!player.isOp()) {
		    				try {
		    					player.setOp(true); //WARNING Run as op feature may be unsafe
			    				player.performCommand(command);
		    					player.setOp(false);
		    				} catch (Exception e) {
		    					Bukkit.getLogger().severe("TokenCommands \"run as op\" feature safety error please contact to plugin owner if you see this message"); //Notify if something went wrong
		    					player.setOp(false);
		    				}
		    				//Bukkit.getLogger().info(player.getName().concat(" issued token command as op: ".concat(command)));
		    				return;
		    			}
		    			player.performCommand(command);
		    			//Bukkit.getLogger().info(player.getName().concat(" issued token command as op: ".concat(command)));
		    			return;
		    		}
		    		player.chat(command); //Run as player
		    		//if(command.startsWith("/")) {
		    		//	Bukkit.getLogger().info(player.getName().concat(" issued token command: ".concat(command)));
		    		//}
		    	}));
		    	toexecuteascommands.clear();
		    }
		}.runTaskTimer(TokenCommands.plugin, 0L, 1);
	}
	
	protected static final void endTask() {
		task.cancel();
		toexecuteascommands.clear();
	}
	
	protected static boolean execute(Player player,String command) {
		ArrayList<String> cmds = toexecuteascommands.containsKey(player) ? toexecuteascommands.get(player) : new ArrayList<String>();
		cmds.add(command);
		toexecuteascommands.put(player, cmds);
		return true;
	}
	
}
