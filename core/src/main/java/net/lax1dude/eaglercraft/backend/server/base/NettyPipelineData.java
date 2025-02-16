package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class NettyPipelineData {

	public final Channel channel;
	public final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;

	public IEaglerListenerInfo listenerInfo;
	public String eaglerBrandString;
	public String eaglerVersionString;
	public UUID eaglerBrandUUID;

	public String headerHost;
	public String headerOrigin;
	public String headerUserAgent;
	public String requestPath;

	public int handshakeProtocol;
	public GamePluginMessageProtocol gameProtocol;
	public boolean cookieEnabled;
	public byte[] cookieData;

	public IPlatformSubLogger connectionLogger;

	public NettyPipelineData(Channel channel, EaglerAttributeManager.EaglerAttributeHolder attributeHolder) {
		this.channel = channel;
		this.attributeHolder = attributeHolder;
	}

	public boolean isEaglerPlayer() {
		return listenerInfo != null;
	}

}
