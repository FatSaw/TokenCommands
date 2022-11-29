package me.bomb.tokencommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.google.gson.JsonSyntaxException;

final class JsonManager_1_7_R4 extends JsonManager {

	@Override
	protected TokenMessage replaceCommands(UUID playeruuid, String icbc) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement ajsonmsgelement = parser.parse(icbc);
			if(ajsonmsgelement.isJsonNull()||!ajsonmsgelement.isJsonObject()) {
				return null;
			}
			JsonObject ajsonmsg = ajsonmsgelement.getAsJsonObject();
			ArrayList<JsonObject> commandelements = new ArrayList<JsonObject>();
    		if (ajsonmsg.has("clickEvent")) {
    			JsonElement jsonmsgelement = ajsonmsg.get("clickEvent");
        		if(!jsonmsgelement.isJsonNull()) {
        			if(jsonmsgelement.isJsonObject()) {
        				JsonObject jo3 = jsonmsgelement.getAsJsonObject();
    					if(jo3.has("action") && jo3.has("value")) {
    						if(jo3.get("action").getAsString().equals("run_command")) {
    							commandelements.add(jo3);
    						}
    					}
        			}
        		}
    		}
    		if(ajsonmsg.has("extra")) {
    			JsonElement jsonmsgelement = ajsonmsg.get("extra");
        		if(!jsonmsgelement.isJsonNull()) {
            		if(jsonmsgelement.isJsonArray()) {
            			JsonArray ja = jsonmsgelement.getAsJsonArray();
                		for(JsonElement jo2element : ja) {
                			if(!jo2element.isJsonNull()) {
                				if(jo2element.isJsonObject()) {
                					JsonObject jo2 = jo2element.getAsJsonObject();
                					JsonElement jo3element = jo2.get("clickEvent");
                    				if(jo2.has("clickEvent")) {
                    					if(!jo3element.isJsonNull()) {
                        					if(jo3element.isJsonObject()) {
                        						JsonObject jo3 = jo3element.getAsJsonObject();
                            					if(jo3.has("action") && jo3.has("value")) {
                            						if(jo3.get("action").getAsString().equals("run_command")) {
                            							commandelements.add(jo3);
                            						}
                            					}
                            				}
                        				}
                    				}
                    			}
                			}
                		}
                	}
            	}
    		}
    		if(commandelements.isEmpty()) {
    			return null;
    		}
    		HashSet<UUID> tokens = replace(playeruuid,commandelements);
    		
    		return new TokenMessage(ajsonmsg.toString(), tokens);
		} catch (JsonSyntaxException e) {
		}
		return null;
	}
	
	private HashSet<UUID> replace(UUID playeruuid,ArrayList<JsonObject> commandobjects) {
		HashMap<Short,HashSet<Short>> cp = new HashMap<Short,HashSet<Short>>();
		HashMap<Short,UUID> pt = new HashMap<Short,UUID>();
		HashSet<UUID> tokens = new HashSet<UUID>();
		HashMap<Short,ArrayList<ArrayList<String>>> properties = new HashMap<Short,ArrayList<ArrayList<String>>>();
		for(short i=0;i<commandobjects.size();i++) {
			JsonObject commandobject = commandobjects.get(i);
			if(commandobject.has("value")) {
				String command = commandobject.get("value").getAsString();
				short property = 0x0100;
				if(command.length()>7&&command.contains("§")) {
					String[] splittokencommandsproperty = command.substring(0, 8).split("§", 5);
					if(splittokencommandsproperty.length==5) {
						StringBuilder sb = new StringBuilder(8);
						sb.append("0000");
						sb.append(splittokencommandsproperty[1]);
						sb.append(splittokencommandsproperty[2]);
						sb.append(splittokencommandsproperty[3]);
						sb.append(splittokencommandsproperty[4]);
						command = command.substring(8);
						try {
							property = (short) Integer.parseInt(sb.toString(), 16);
						} catch (NumberFormatException e) {
						}
					}
				}
				ArrayList<String> acommands = new ArrayList<String>();
				if(command.contains("&&&")) {
					String[] cmds = command.split("&&&",127);
					for(byte j=0;j<cmds.length;++j) {
						acommands.add(cmds[j]);
					}
				} else {
					acommands.add(command);
				}
				ArrayList<ArrayList<String>> tokenprops = properties.containsKey(property)?properties.get(property):new ArrayList<ArrayList<String>>();
				tokenprops.add(acommands);
				properties.put(property, tokenprops);
				commandobject.remove("value");
				HashSet<Short> c = cp.containsKey(property) ? cp.get(property) : new HashSet<Short>();
				c.add(i);
				cp.put(property, c);
			}
		}
		properties.keySet().forEach(property -> {
			byte time = (byte) (0xFF &property);
			byte uses = (byte)(property>>8);
			//Bukkit.getLogger().warning("Time: " + time + " Uses: " + uses);
			UUID token = TokenCommand.put(playeruuid, properties.get(property), uses, time);
			pt.put(property, token);
		});
		//Bukkit.getLogger().warning("Commands: " + cp.size() + " Tokens: " + pt.size());
		for (short property : cp.keySet()) {
			if(pt.containsKey(property)) {
				UUID token = pt.get(property);
				short cmdnum=0;
				for(short msgnum : cp.get(property)) {
					commandobjects.get(msgnum).addProperty("value", "/////".concat(token.toString()).concat(Integer.toHexString(-33 & cmdnum)));
					++cmdnum;
				}
				tokens.add(token);
			}
		}
		return tokens;
	}
}
