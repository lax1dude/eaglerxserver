package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketGetOtherTexturesV5EAG;

public class ServerV5MessageHandler extends ServerV4MessageHandler {

	public ServerV5MessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		super(eaglerHandle);
	}

	public void handleClient(CPacketGetOtherTexturesV5EAG packet) {
		eaglerHandle.getSkinManager().handlePacketGetTextures(packet.uuidMost, packet.uuidLeast);
	}

}
