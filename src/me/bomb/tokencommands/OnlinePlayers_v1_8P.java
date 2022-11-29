package me.bomb.tokencommands;

import org.bukkit.Bukkit;

final class OnlinePlayers_v1_8P extends OnlinePlayers {

	@Override
	protected void enable() {
		CommandManager.enable(Bukkit.getOnlinePlayers());
	}

	@Override
	protected void disable() {
		CommandManager.disable(Bukkit.getOnlinePlayers());
	}

}
