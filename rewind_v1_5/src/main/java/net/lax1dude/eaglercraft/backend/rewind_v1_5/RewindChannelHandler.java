package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public class RewindChannelHandler<PlayerObject> extends MessageToMessageCodec<ByteBuf, ByteBuf> {

	protected final PlayerInstance<PlayerObject> player;
	protected Codec<PlayerObject> encoder;
	protected Codec<PlayerObject> decoder;

	public static abstract class Codec<PlayerObject> {

		private RewindChannelHandler<PlayerObject> codec;

		protected abstract void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception;

		protected abstract void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception;

		protected final RewindChannelHandler<PlayerObject> handler() {
			return codec;
		}

		protected final PlayerInstance<PlayerObject> player() {
			return codec.player;
		}

		protected final RewindPluginProtocol<PlayerObject> rewind() {
			return codec.player.getRewind();
		}

		protected final IEaglerXServerAPI<PlayerObject> serverAPI() {
			return codec.player.getRewind().getServerAPI();
		}

	}

	public static abstract class Encoder<PlayerObject> extends Codec<PlayerObject> {

		protected final void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
			throw new IllegalStateException();
		}

	}

	public static abstract class Decoder<PlayerObject> extends Codec<PlayerObject> {

		protected final void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
			throw new IllegalStateException();
		}

	}

	public RewindChannelHandler(PlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	public RewindChannelHandler<PlayerObject> setEncoder(Codec<PlayerObject> encoder) {
		encoder.codec = this;
		this.encoder = encoder;
		return this;
	}

	public RewindChannelHandler<PlayerObject> setDecoder(Codec<PlayerObject> decoder) {
		decoder.codec = this;
		this.decoder = decoder;
		return this;
	}

	public RewindChannelHandler<PlayerObject> setCodec(Codec<PlayerObject> codec) {
		codec.codec = this;
		this.encoder = codec;
		this.decoder = codec;
		return this;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		decoder.decode(ctx, buf, out);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		encoder.encode(ctx, buf, out);
	}

}
