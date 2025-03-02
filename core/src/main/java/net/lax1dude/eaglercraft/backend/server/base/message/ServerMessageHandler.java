package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformComponentBuilder.EnumChatColor;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public abstract class ServerMessageHandler implements MessageController.IMessageHandler {

	protected final EaglerPlayerInstance<?> eaglerHandle;

	public ServerMessageHandler(EaglerPlayerInstance<?> eaglerHandle) {
		this.eaglerHandle = eaglerHandle;
	}

	public EaglerXServer<?> getServer() {
		return eaglerHandle.getEaglerXServer();
	}

	@Override
	public void handleException(Exception ex) {
		EaglerXServer<?> server = getServer();
		server.logger().error("Exception thrown while handling eagler packet for \"" + eaglerHandle.getUsername() + "\"!", ex);
		eaglerHandle.disconnect(server.componentBuilder().buildTextComponent().beginStyle().color(EnumChatColor.RED)
				.end().text("Eaglercraft Packet Error").end());
	}

}
