package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformConnection;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPendingConnection;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class EaglerConnectionInstance extends BaseConnectionInstance implements IEaglerPendingConnection {

	private final Channel channel;
	private final IEaglerListenerInfo listenerInfo;
	private final String eaglerBrandString;
	private final String eaglerVersionString;
	private final UUID eaglerBrandUUID;
	private final String headerHost;
	private final String headerOrigin;
	private final String headerUserAgent;
	private final String requestPath;
	private final int handshakeProtocol;
	private final GamePluginMessageProtocol gameProtocol;
	private final boolean cookieEnabled;
	private byte[] cookieDataInit;
	private final IPlatformSubLogger connectionLogger;

	public EaglerConnectionInstance(IPlatformConnection connection,
			NettyPipelineData pipelineData) {
		super(connection, pipelineData.attributeHolder);
		this.channel = pipelineData.channel;
		this.listenerInfo = pipelineData.listenerInfo;
		this.eaglerBrandString = pipelineData.eaglerBrandString;
		this.eaglerVersionString = pipelineData.eaglerVersionString;
		this.eaglerBrandUUID = pipelineData.eaglerBrandUUID;
		this.headerHost = pipelineData.headerHost;
		this.headerOrigin = pipelineData.headerOrigin;
		this.headerUserAgent = pipelineData.headerUserAgent;
		this.requestPath = pipelineData.requestPath;
		this.handshakeProtocol = pipelineData.handshakeProtocol;
		this.gameProtocol = pipelineData.gameProtocol;
		this.cookieEnabled = pipelineData.cookieEnabled;
		this.cookieDataInit = pipelineData.cookieData;
		this.connectionLogger = pipelineData.connectionLogger;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

	@Override
	public IEaglerListenerInfo getListenerInfo() {
		return listenerInfo;
	}

	@Override
	public String getWebSocketHeader(EnumWebSocketHeader header) {
		switch(header) {
		case HEADER_HOST:
			return headerHost;
		case HEADER_ORIGIN:
			return headerOrigin;
		case HEADER_USER_AGENT:
			return headerUserAgent;
		case REQUEST_PATH:
			return requestPath;
		default:
			return null;
		}
	}

	@Override
	public String getEaglerBrandName() {
		return eaglerBrandString;
	}

	@Override
	public String getEaglerVersionName() {
		return eaglerVersionString;
	}

	@Override
	public UUID getEaglerBrandUUID() {
		return eaglerBrandUUID;
	}

	@Override
	public int getHandshakeEaglerProtocol() {
		return handshakeProtocol;
	}

	@Override
	public GamePluginMessageProtocol getEaglerProtocol() {
		return gameProtocol;
	}

	public Channel channel() {
		return channel;
	}

	public IPlatformSubLogger logger() {
		return connectionLogger;
	}

	public boolean cookieEnabled() {
		return cookieEnabled;
	}

	public byte[] transferCookieData() {
		byte[] ret = cookieDataInit;
		cookieDataInit = null;
		return ret;
	}

}
