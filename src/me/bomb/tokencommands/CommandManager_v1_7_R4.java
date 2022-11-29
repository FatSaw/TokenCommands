package me.bomb.tokencommands;

import java.lang.reflect.Field;
import java.util.Collection;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelPipeline;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketDataSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayInChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;

final class CommandManager_v1_7_R4 extends CommandManager {
	
	protected CommandManager_v1_7_R4() {
		outchatpacket = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"\"}"));
	}
	
	@Override
	protected void register(Player player) {
		//ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
		ChannelPipeline pipeline = null;
		try {
			Field channelField = NetworkManager.class.getDeclaredField("m");
		    channelField.setAccessible(true);
		    Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
		    channelField.setAccessible(false);
		    pipeline = channel.pipeline();
		    
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		if(pipeline==null) {
			return;
		}
		ChannelDuplexHandler outputChannelHandler = new ChannelDuplexHandler();
	    pipeline.addBefore("packet_handler", "outputtokencommands", outputChannelHandler);
		MessageCache cache = new MessageCache(player.getUniqueId(),pipeline.context(outputChannelHandler));
	    ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
		@Override
        public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        	if (packet instanceof PacketPlayInChat) {
        		PacketPlayInChat info = (PacketPlayInChat) packet;
        		String command = info.c();
        		if(CommandManager.read(player, command)) {
        			return;
        		}
        	}
        	super.channelRead(context, packet);
        }
        @Override
        public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
        	if(packet instanceof PacketPlayOutChat) {
            	PacketPlayOutChat info = (PacketPlayOutChat) packet;
            	if(info.d()) {
            		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(32767));
            		info.b(packetdataserializer);
            		TokenMessage tm = JsonManager.write(player.getUniqueId(), packetdataserializer.c(32767));
                	if(tm!=null) {
	                	packetdataserializer.a(tm.msg);
	                	info.a(packetdataserializer);
	                	cache.addHolddown(tm.tokens, info);
                	} else {
                		if(cache.add(info)) {
                			return;
                		}
                	}
            	}
            }
        	super.write(context, packet, channelPromise);
        }
    };
    pipeline.addAfter("outputtokencommands", "tokencommands", channelDuplexHandler);
	}
	
	@Override
	protected void resend(Collection<Object> cachedmessages,Object context) {
		if(context==null||cachedmessages==null) {
			return;
		}
		for(Object packet : cachedmessages) {
			((ChannelHandlerContext)context).write(packet);
		}
	}

	@Override
	protected void unregister(Player player) {
		try {
			Field channelField = NetworkManager.class.getDeclaredField("m");
		    channelField.setAccessible(true);
		    Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
		    channelField.setAccessible(false);
		    channel.eventLoop().submit(() -> {
	            channel.pipeline().remove("outputtokencommands");
	            channel.pipeline().remove("tokencommands");
	            return null;
	        });
		} catch (NoSuchFieldException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}

}
