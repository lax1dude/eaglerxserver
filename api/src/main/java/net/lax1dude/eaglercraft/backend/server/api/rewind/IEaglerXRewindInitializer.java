package net.lax1dude.eaglercraft.backend.server.api.rewind;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IEaglerXRewindInitializer<Attachment> {

	IEaglerConnection getConnection();

	void setAttachment(Attachment obj);

	default int getLegacyMinecraftProtocol() {
		return getLegacyHandshake().getProtocolVersion();
	}

	IPacket2ClientProtocol getLegacyHandshake();

	void rewriteInitialHandshakeV1(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand, String eaglerClientVersion);

	void rewriteInitialHandshakeV2(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand, String eaglerClientVersion, boolean authEnabled, byte[] authUsername);

	void cancelDisconnect();

	NettyUnsafe netty();

	public interface NettyUnsafe {

		Channel getChannel();

		void injectNettyHandlers(ChannelOutboundHandler nettyEncoder, ChannelInboundHandler nettyDecoder);

		void injectNettyHandlers(ChannelHandler nettyCodec);

	}

}
