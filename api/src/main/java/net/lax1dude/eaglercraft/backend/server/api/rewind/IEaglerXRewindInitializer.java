package net.lax1dude.eaglercraft.backend.server.api.rewind;

import java.net.SocketAddress;
import java.util.List;

import net.lax1dude.eaglercraft.backend.server.api.EnumWebSocketHeader;

public interface IEaglerXRewindInitializer<Attachment> {

	void setAttachment(Attachment obj);

	SocketAddress getSocketAddress();

	String getRealAddress();

	String getWebSocketHeader(EnumWebSocketHeader header);

	default int getLegacyMinecraftProtocol() {
		return getLegacyHandshake().getProtocolVersion();
	}

	IPacket2ClientProtocol getLegacyHandshake();

	void injectNettyEncoder(Object nettyEncoder);

	void injectNettyDecoder(Object nettyDecoder);

	Object getNettyChannel();

	List<Object> getWaitingOutboundFrames();

	void cancelDisconnect();

}
