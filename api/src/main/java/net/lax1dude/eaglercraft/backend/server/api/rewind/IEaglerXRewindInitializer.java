/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
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
