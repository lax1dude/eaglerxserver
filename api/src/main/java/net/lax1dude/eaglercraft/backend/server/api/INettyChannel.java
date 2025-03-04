package net.lax1dude.eaglercraft.backend.server.api;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

public interface INettyChannel {

	NettyUnsafe getNettyUnsafe();

	public interface NettyUnsafe {

		Channel getChannel();

		default void writePacket(Object packet) {
			Channel channel = getChannel();
			if(channel.isActive()) {
				channel.writeAndFlush(packet, channel.voidPromise());
			}else {
				ReferenceCountUtil.release(packet);
			}
		}

	}

}
