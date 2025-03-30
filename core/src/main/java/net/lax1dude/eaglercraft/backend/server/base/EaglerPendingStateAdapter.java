package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.backend.server.api.attribute.IAttributeKey;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class EaglerPendingStateAdapter extends IIdentifiedConnection.Base implements IEaglerPendingConnection {

	protected final NettyPipelineData pipelineData;

	EaglerPendingStateAdapter(NettyPipelineData pipelineData) {
		this.pipelineData = pipelineData;
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return pipelineData.listenerInfo;
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		return pipelineData.getWebSocketHeader(header);
	}

	@Override
	public boolean isWebSocketSecure() {
		return pipelineData.wss;
	}

	@Override
	public SocketAddress getSocketAddress() {
		return pipelineData.getSocketAddress();
	}

	@Override
	public SocketAddress getPlayerAddress() {
		return pipelineData.getPlayerAddress();
	}

	@Override
	public String getRealAddress() {
		return pipelineData.realAddress;
	}

	@Override
	public boolean isConnected() {
		return pipelineData.isConnected();
	}

	@Override
	public void disconnect() {
		pipelineData.disconnect();
	}

	@Override
	public NettyUnsafe netty() {
		return pipelineData;
	}

	@Override
	public Object getIdentityToken() {
		return pipelineData.getIdentityToken();
	}

	@Override
	public <T> T get(IAttributeKey<T> key) {
		return pipelineData.attributeHolder.get(key);
	}

	@Override
	public <T> void set(IAttributeKey<T> key, T value) {
		pipelineData.attributeHolder.set(key, value);
	}

	@Override
	public int getMinecraftProtocol() {
		return pipelineData.minecraftProtocol;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerPendingConnection asEaglerPlayer() {
		return this;
	}

	@Override
	public boolean isHandshakeAuthEnabled() {
		return pipelineData.handshakeAuthEnabled;
	}

	@Override
	public byte[] getAuthUsername() {
		return pipelineData.handshakeAuthUsername;
	}

	@Override
	public boolean isEaglerXRewindPlayer() {
		return pipelineData.rewindProtocol != null;
	}

	@Override
	public int getRewindProtocolVersion() {
		return pipelineData.rewindProtocolVersion;
	}

	@Override
	public String getEaglerVersionString() {
		return pipelineData.eaglerVersionString;
	}

	@Override
	public String getEaglerBrandString() {
		return pipelineData.eaglerBrandString;
	}

	@Override
	public int getHandshakeEaglerProtocol() {
		return pipelineData.handshakeProtocol;
	}

	@Override
	public GamePluginMessageProtocol getEaglerProtocol() {
		return pipelineData.gameProtocol;
	}

}
