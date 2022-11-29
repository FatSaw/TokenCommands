package me.bomb.tokencommands;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_7_R4.MinecraftServer;

final class OnlinePlayers_v1_7_R4 extends OnlinePlayers {

	@Override
	protected void enable() {
		HashSet<Player> players = new HashSet<Player>();
		for(String playername : MinecraftServer.getServer().getPlayers()) {
			Player player = Bukkit.getPlayerExact(playername);
			if(player!=null) players.add(player);
		}
		CommandManager.enable(players);
	}

	@Override
	protected void disable() {
		HashSet<Player> players = new HashSet<Player>();
		for(String playername : MinecraftServer.getServer().getPlayers()) {
			Player player = Bukkit.getPlayerExact(playername);
			if(player!=null) players.add(player);
		}
		CommandManager.disable(players);
	}

}
