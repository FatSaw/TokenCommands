package me.bomb.tokencommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

final class MessageCache {
	
	//private static final Object outchatpacket;
	private final Object outputcontext;
	private static HashMap<UUID,MessageCache> playeroutputmessages = new HashMap<UUID,MessageCache>();
	private ArrayList<Object> cache = new ArrayList<Object>(100);
	private HashMap<UUID,Object> holddown = new HashMap<UUID,Object>(100);
	
	/*static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_16_R3":
			outchatpacket = new net.minecraft.server.v1_16_R3.PacketPlayOutChat(net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"),net.minecraft.server.v1_16_R3.ChatMessageType.SYSTEM,null);
			break;
		case "v1_15_R1":
			outchatpacket = new net.minecraft.server.v1_15_R1.PacketPlayOutChat(net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_14_R1":
			outchatpacket = new net.minecraft.server.v1_14_R1.PacketPlayOutChat(net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_13_R2":
			outchatpacket = new net.minecraft.server.v1_13_R2.PacketPlayOutChat(net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_12_R1":
			outchatpacket = new net.minecraft.server.v1_12_R1.PacketPlayOutChat(net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_11_R1":
			outchatpacket = new net.minecraft.server.v1_11_R1.PacketPlayOutChat(net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_10_R1":
			outchatpacket = new net.minecraft.server.v1_10_R1.PacketPlayOutChat(net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_9_R2":
			outchatpacket = new net.minecraft.server.v1_9_R2.PacketPlayOutChat(net.minecraft.server.v1_9_R2.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_8_R3":
			outchatpacket = new net.minecraft.server.v1_8_R3.PacketPlayOutChat(net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		case "v1_7_R4":
			outchatpacket = new net.minecraft.server.v1_7_R4.PacketPlayOutChat(net.minecraft.server.v1_7_R4.ChatSerializer.a("{\"text\":\"\"}"));
			break;
		default:
			outchatpacket = null;
		}
	}*/
	
	MessageCache(UUID playeruuid,Object outputcontext) {
		for(byte i=0;i<100;++i) {
			cache.add(CommandManager.outchatpacket);
			//holddownorder.add(null);
		}
		playeroutputmessages.put(playeruuid, this);
		this.outputcontext = outputcontext;
	}
	
	protected boolean add(Object message) {
		Collections.rotate(cache, -1);
		cache.set(99, message);
		if(!holddown.isEmpty()) {
			resendChat();
			return true;
		}
		return false;
	}
	
	protected void addHolddown(HashSet<UUID> tokens,Object message) {
		tokens.forEach(token -> {
			holddown.put(token, message);
			//Bukkit.getLogger().warning("Added " + token.toString());
		});
	}
	
	protected static void removeHolddown(UUID uuid,UUID token) {
		if(!playeroutputmessages.containsKey(uuid)) {
			return;
		}
		//Bukkit.getLogger().warning("Remove " + token.toString());
		MessageCache mc = playeroutputmessages.get(uuid);
		mc.holddown.remove(token);
		mc.resendChat();
	}
	
	private void resendChat() {
		CommandManager.commandmanager.resend(cache,outputcontext);
		if(!holddown.isEmpty()) {
			HashSet<Object> holddownnorepeat = new HashSet<Object>();
			holddown.values().forEach(holddownmsg -> holddownnorepeat.add(holddownmsg));
			CommandManager.commandmanager.resend(holddownnorepeat,outputcontext);
		}
	}
	
	protected static void removeLogout(UUID uuid) {
		playeroutputmessages.remove(uuid);
	}
	
}
