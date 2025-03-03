package net.lax1dude.eaglercraft.backend.server.api.rewind;

import java.net.SocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;

public interface IEaglerXRewindInitializer<Attachment> {

	void setAttachment(Attachment obj);

	SocketAddress getSocketAddress();

	String getRealAddress();

	String getWebSocketHeader(EnumWebSocketHeader header);

	default int getLegacyMinecraftProtocol() {
		return getLegacyHandshake().getProtocolVersion();
	}

	IPacket2ClientProtocol getLegacyHandshake();

	void rewriteInitialHandshakeV1(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand, String eaglerClientVersion);

	void rewriteInitialHandshakeV2(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand, String eaglerClientVersion, boolean authEnabled, byte[] authUsername);

	void cancelDisconnect();

	NettyUnsafe getNettyUnsafe();

	public interface NettyUnsafe {

		Channel getChannel();

		void injectNettyHandlers(ChannelOutboundHandler nettyEncoder, ChannelInboundHandler nettyDecoder);

		void injectNettyHandlers(ChannelHandler nettyCodec);

	}

}
