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

package net.lax1dude.eaglercraft.backend.server.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerConnection;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindInitializer;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IOutboundInjector;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IPacket2ClientProtocol;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageControllerHandle;
import net.lax1dude.eaglercraft.backend.server.base.message.RewindMessageInjector;

public abstract class RewindInitializer<Attachment> implements IEaglerXRewindInitializer<Attachment>,
		IEaglerXRewindInitializer.NettyUnsafe {

	private static class Packet2ClientProtocol implements IPacket2ClientProtocol {

		private final int protocolVersion;
		private final String username;
		private final String serverHost;
		private final int serverPort;

		protected Packet2ClientProtocol(int protocolVersion, String username, String serverHost, int serverPort) {
			this.protocolVersion = protocolVersion;
			this.username = username;
			this.serverHost = serverHost;
			this.serverPort = serverPort;
		}

		@Override
		public int getProtocolVersion() {
			return protocolVersion;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public String getServerHost() {
			return serverHost;
		}

		@Override
		public int getServerPort() {
			return serverPort;
		}

	}

	protected final Channel channel;
	private final NettyPipelineData pipelineData;
	private final int protocolVersion;
	private final String username;
	private final String serverHost;
	private final int serverPort;

	private Packet2ClientProtocol packetAdapter;
	private boolean injected;
	private boolean handshake;
	private boolean canceled;

	private int eaglerProtocol;
	private int minecraftProtocol;
	private String eaglerClientBrand;
	private String eaglerClientVersion;
	private boolean authEnabled;
	private byte[] authUsername;

	private RewindMessageControllerHandle messageController;
	private RewindMessageInjector messageInjector;

	public RewindInitializer(Channel channel, NettyPipelineData pipelineData, int protocolVersion, String username,
			String serverHost, int serverPort) {
		this.channel = channel;
		this.pipelineData = pipelineData;
		this.protocolVersion = protocolVersion;
		this.username = username;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}

	@Override
	public IEaglerConnection getConnection() {
		return pipelineData;
	}

	@Override
	public void setAttachment(Attachment obj) {
		pipelineData.rewindAttachment = obj;
	}

	@Override
	public IPacket2ClientProtocol getLegacyHandshake() {
		if(packetAdapter == null) {
			packetAdapter = new Packet2ClientProtocol(protocolVersion, username, serverHost, serverPort);
		}
		return packetAdapter;
	}

	@Override
	public void rewriteInitialHandshakeV1(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand, String eaglerClientVersion) {
		if(eaglerProtocol != 1) {
			throw new IllegalArgumentException("Invalid eagler protocol " + eaglerProtocol + " for V1 handshake, must be 1");
		}
		if(minecraftProtocol < 0 || minecraftProtocol > 255) {
			throw new IllegalArgumentException("Invalid minecraft protocol " + minecraftProtocol + " for V1 handshake, must be between 0 and 255");
		}
		if(eaglerClientBrand == null) {
			throw new NullPointerException("eaglerClientBrand");
		}
		if(eaglerClientVersion == null) {
			throw new NullPointerException("eaglerClientVersion");
		}
		if(handshake) {
			throw new IllegalStateException("Handshake has already been injected");
		}
		handshake = true;
		this.eaglerProtocol = eaglerProtocol;
		this.minecraftProtocol = minecraftProtocol;
		this.eaglerClientBrand = eaglerClientBrand;
		this.eaglerClientVersion = eaglerClientVersion;
		this.authEnabled = false;
		this.authUsername = null;
	}

	@Override
	public void rewriteInitialHandshakeV2(int eaglerProtocol, int minecraftProtocol, String eaglerClientBrand,
			String eaglerClientVersion, boolean authEnabled, byte[] authUsername) {
		if(eaglerProtocol <= 1) {
			throw new IllegalArgumentException("Invalid eagler protocol for V2+ handshake: " + eaglerProtocol);
		}
		if((authEnabled || eaglerProtocol >= 5) && authUsername == null) {
			throw new NullPointerException("Auth username is null");
		}
		if(eaglerClientBrand == null) {
			throw new NullPointerException("eaglerClientBrand");
		}
		if(eaglerClientVersion == null) {
			throw new NullPointerException("eaglerClientVersion");
		}
		if(handshake) {
			throw new IllegalStateException("Handshake has already been injected");
		}
		handshake = true;
		this.eaglerProtocol = eaglerProtocol;
		this.minecraftProtocol = minecraftProtocol;
		this.eaglerClientBrand = eaglerClientBrand;
		this.eaglerClientVersion = eaglerClientVersion;
		this.authEnabled = authEnabled;
		this.authUsername = authUsername;
	}

	@Override
	public void cancelDisconnect() {
		canceled = true;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public void injectNettyHandlers(ChannelOutboundHandler nettyEncoder, ChannelInboundHandler nettyDecoder) {
		if(nettyEncoder == null) {
			throw new NullPointerException("nettyEncoder");
		}
		if(nettyDecoder == null) {
			throw new NullPointerException("nettyDecoder");
		}
		if(injected) {
			throw new IllegalStateException("Handlers have already been injected");
		}
		injected = true;
		injectNettyHandlers0(nettyEncoder, nettyDecoder);
	}

	protected abstract void injectNettyHandlers0(ChannelOutboundHandler nettyEncoder, ChannelInboundHandler nettyDecoder);

	@Override
	public void injectNettyHandlers(ChannelHandler nettyCodec) {
		if(nettyCodec == null) {
			throw new NullPointerException("nettyCodec");
		}
		if(injected) {
			throw new IllegalStateException("Handlers have already been injected");
		}
		injected = true;
		injectNettyHandlers0(nettyCodec);
	}

	protected abstract void injectNettyHandlers0(ChannelHandler nettyCodec);

	@Override
	public IMessageController requestMessageController() {
		if(messageController != null) {
			throw new IllegalStateException("Message controller handle has already been created");
		}
		return messageController = new RewindMessageControllerHandle(pipelineData.connectionLogger);
	}

	@Override
	public IOutboundInjector requestOutboundInjector() {
		if(messageInjector != null) {
			throw new IllegalStateException("Message injector has already been created");
		}
		return messageInjector = new RewindMessageInjector(channel);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public boolean isInjected() {
		return injected;
	}

	public boolean isHandshake() {
		return handshake;
	}

	public int getEaglerProtocol() {
		return eaglerProtocol;
	}

	public int getMinecraftProtocol() {
		return minecraftProtocol;
	}

	public String getEaglerClientBrand() {
		return eaglerClientBrand;
	}

	public String getEaglerClientVersion() {
		return eaglerClientVersion;
	}

	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public byte[] getAuthUsername() {
		return authUsername;
	}

	public RewindMessageControllerHandle getMessageControllerHandle() {
		return messageController;
	}

	public RewindMessageInjector getMessageInjector() {
		return messageInjector;
	}

}
