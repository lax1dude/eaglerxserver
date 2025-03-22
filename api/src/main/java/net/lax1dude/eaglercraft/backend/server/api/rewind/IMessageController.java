package net.lax1dude.eaglercraft.backend.server.api.rewind;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IMessageController {

	void setOutboundHandler(GameMessageHandler handler);

	void recieveInboundMessage(GameMessagePacket packet);

}
