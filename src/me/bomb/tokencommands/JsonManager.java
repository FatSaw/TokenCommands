package me.bomb.tokencommands;

import java.util.UUID;

import org.bukkit.Bukkit;

abstract class JsonManager {
	
	private static final JsonManager jsonmanager;
	
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":
		case "v1_15_R1":
		case "v1_14_R1":
		case "v1_13_R2":
		case "v1_12_R1":
		case "v1_11_R1":
		case "v1_10_R1":
		case "v1_9_R2":
		case "v1_8_R3":
			jsonmanager = new JsonManager_1_8P();
			break;
		case "v1_7_R4":
			jsonmanager = new JsonManager_1_7_R4();
			break;
		default:
			jsonmanager = null;
		}
	}
	
	protected static final TokenMessage write(UUID playeruuid,String icbc) {
		return jsonmanager.replaceCommands(playeruuid, icbc);
	}
	
	protected abstract TokenMessage replaceCommands(UUID playeruuid,String icbc);
	
}
