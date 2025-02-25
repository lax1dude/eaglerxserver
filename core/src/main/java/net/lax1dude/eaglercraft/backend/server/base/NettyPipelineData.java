package net.lax1dude.eaglercraft.backend.server.base;

import java.util.UUID;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformSubLogger;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IEaglerXRewindProtocol;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class NettyPipelineData {

	public final Channel channel;
	public final EaglerAttributeManager.EaglerAttributeHolder attributeHolder;

	public EaglerListener listenerInfo;
	public String eaglerBrandString;
	public String eaglerVersionString;
	public UUID eaglerBrandUUID;

	public boolean wss;
	public String headerHost;
	public String headerOrigin;
	public String headerUserAgent;
	public String headerCookie;
	public String headerAuthorization;
	public String requestPath;
	public String realAddress;

	public int handshakeProtocol;
	public GamePluginMessageProtocol gameProtocol;
	public boolean cookieEnabled;
	public byte[] cookieData;

	public IPlatformSubLogger connectionLogger;

	public Object rewindAttachment;
	public IEaglerXRewindProtocol<?, ?> rewindProtocol;

	public NettyPipelineData(Channel channel, EaglerListener listenerInfo,
			EaglerAttributeManager.EaglerAttributeHolder attributeHolder) {
		this.channel = channel;
		this.listenerInfo = listenerInfo;
		this.attributeHolder = attributeHolder;
	}

	public boolean isEaglerPlayer() {
		return listenerInfo != null;
	}

}
