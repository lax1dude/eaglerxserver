package net.lax1dude.eaglercraft.backend.server.api.rewind;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public interface IMessageController {

	void setOutboundHandler(@Nonnull GameMessageHandler handler);

	void recieveInboundMessage(@Nonnull GameMessagePacket packet);

}
