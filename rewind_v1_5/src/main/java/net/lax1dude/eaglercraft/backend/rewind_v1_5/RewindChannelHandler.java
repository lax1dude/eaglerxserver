/*
 * Copyright (c) 2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.rewind_v1_5;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.lax1dude.eaglercraft.backend.server.api.IComponentHelper;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.nbt.INBTContext;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;

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

		protected final IRewindLogger logger() {
			return codec.player.logger();
		}

		protected final IEaglerXServerAPI<PlayerObject> serverAPI() {
			return codec.player.getRewind().getServerAPI();
		}

		protected final INBTContext nbtContext() {
			return codec.player.getNBTContext();
		}

		protected final IComponentHelper componentHelper() {
			return codec.player.getComponentHelper();
		}

		protected final IMessageController messageController() {
			return codec.player.getMessageController();
		}

		protected final TabListTracker tabList() {
			return codec.player.getTabList();
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

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		player.releaseNatives();
	}

}
