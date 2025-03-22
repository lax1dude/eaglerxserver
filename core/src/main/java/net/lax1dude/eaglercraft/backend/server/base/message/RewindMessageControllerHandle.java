package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.rewind.IMessageController;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;

public class RewindMessageControllerHandle implements IMessageController {

	IPlatformLogger logger;
	GameMessageHandler handler;
	RewindMessageControllerImpl impl;

	public RewindMessageControllerHandle(IPlatformLogger logger) {
		this.logger = logger;
	}

	@Override
	public void setOutboundHandler(GameMessageHandler handler) {
		this.handler = handler;
	}

	@Override
	public void recieveInboundMessage(GameMessagePacket packet) {
		if(impl != null) {
			impl.handlePacket(packet);
		}else {
			logger.error("Dropping inbound packet " + packet.getClass().getSimpleName()
					+ " on rewind connection because the connection is not ready!");
		}
	}

	void recieveOutboundMessage(GameMessagePacket packet) {
		if(handler != null) {
			packet.handlePacket(handler);
		}else {
			logger.error("Dropping outbound packet " + packet.getClass().getSimpleName()
					+ " on rewind connection because no handler is registered!");
		}
	}

}
