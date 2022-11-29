package me.bomb.tokencommands;

import java.util.Collection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

final class CommandManager_v1_8_R3 extends CommandManager {
	
	protected CommandManager_v1_8_R3() {
		outchatpacket = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"\"}"));
	}
	
	@Override
	protected void register(Player player) {
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
		ChannelDuplexHandler outputChannelHandler = new ChannelDuplexHandler();
	    pipeline.addBefore("packet_handler", "outputtokencommands", outputChannelHandler);
		MessageCache cache = new MessageCache(player.getUniqueId(),pipeline.context(outputChannelHandler));
	    ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
		@Override
        public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        	if (packet instanceof PacketPlayInChat) {
        		PacketPlayInChat info = (PacketPlayInChat) packet;
        		String command = info.a();
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
            	if(info.b()) {
            		PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer(0));
            		info.b(packetdataserializer);
            		TokenMessage tm = JsonManager.write(player.getUniqueId(), packetdataserializer.c(32767));
                	if(tm!=null) {
	                	packetdataserializer.a(tm.msg);
	                    packetdataserializer.writeByte(packetdataserializer.readByte());
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
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("outputtokencommands");
            channel.pipeline().remove("tokencommands");
            return null;
        });
	}

}
