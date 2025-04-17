package net.lax1dude.eaglercraft.backend.server.api;

import java.net.SocketAddress;

import javax.annotation.Nonnull;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

public interface INettyChannel {

	@Nonnull
	SocketAddress getSocketAddress();

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		Channel getChannel();

		default void writePacket(@Nonnull Object packet) {
			if(packet == null) {
				throw new NullPointerException("packet");
			}
			Channel channel = getChannel();
			if(channel.isActive()) {
				channel.writeAndFlush(packet, channel.voidPromise());
			}else {
				ReferenceCountUtil.release(packet);
			}
		}

	}

}
