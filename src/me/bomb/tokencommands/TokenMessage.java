package me.bomb.tokencommands;

import java.util.HashSet;
import java.util.UUID;

final class TokenMessage {
	
	protected final String msg;
	protected final HashSet<UUID> tokens;
	
	TokenMessage(String msg,HashSet<UUID> tokens) {
		this.msg = msg;
		this.tokens = tokens;
	}
	
}
