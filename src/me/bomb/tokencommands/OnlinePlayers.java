package me.bomb.tokencommands;

import org.bukkit.Bukkit;


abstract class OnlinePlayers {
	private static final OnlinePlayers onlineplayers;
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":case "v1_15_R1":case "v1_14_R1":case "v1_13_R2":case "v1_12_R1":case "v1_11_R1":case "v1_10_R1":case "v1_9_R2":case "v1_8_R3":
			onlineplayers = new OnlinePlayers_v1_8P();
			break;
		case "v1_7_R4":
			onlineplayers = new OnlinePlayers_v1_7_R4();
			break;
		default:
			onlineplayers = null;
		}
	}
	protected static void onlineplenable() {
		onlineplayers.enable();
	}
	protected static void onlinepldisable() {
		onlineplayers.disable();
	}
	protected abstract void enable();
	protected abstract void disable();
}
