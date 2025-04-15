package net.lax1dude.eaglercraft.backend.server.api.rewind;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;

public interface IEaglerXRewindInitializer<Attachment> {

	@Nonnull
	IEaglerConnection getConnection();

	void setAttachment(@Nullable Attachment obj);

	default int getLegacyMinecraftProtocol() {
		return getLegacyHandshake().getProtocolVersion();
	}

	@Nonnull
	IPacket2ClientProtocol getLegacyHandshake();

	void rewriteInitialHandshakeV1(int eaglerProtocol, int minecraftProtocol, @Nonnull String eaglerClientBrand,
			@Nonnull String eaglerClientVersion);

	void rewriteInitialHandshakeV2(int eaglerProtocol, int minecraftProtocol, @Nonnull String eaglerClientBrand,
			@Nonnull String eaglerClientVersion, boolean authEnabled, @Nullable byte[] authUsername);

	@Nonnull
	IMessageController requestMessageController();

	@Nonnull
	IOutboundInjector requestOutboundInjector();

	void cancelDisconnect();

	@Nonnull
	NettyUnsafe netty();

	public interface NettyUnsafe {

		@Nonnull
		Channel getChannel();

		void injectNettyHandlers(@Nonnull ChannelOutboundHandler nettyEncoder,
				@Nonnull ChannelInboundHandler nettyDecoder);

		void injectNettyHandlers(@Nonnull ChannelHandler nettyCodec);

	}

}
