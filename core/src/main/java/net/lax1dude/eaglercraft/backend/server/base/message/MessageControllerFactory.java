package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class MessageControllerFactory {

	public static MessageController initializePlayer(EaglerPlayerInstance<?> instance) {
		GamePluginMessageProtocol protocol = instance.getEaglerProtocol();
		ServerMessageHandler handler = createHandler(protocol.ver, instance);
		EaglerXServer<?> server = instance.getEaglerXServer();
		int sendDelay = server.getConfig().getSettings().getProtocolV4DefragSendDelay();
		if (protocol.ver >= 5) {
			return InjectedMessageController.injectEagler(protocol, handler,
					instance.getPlatformPlayer().getConnection().getChannel(), sendDelay);
		}else {
			if (sendDelay > 0) {
				return new LegacyMessageController(protocol, handler,
						instance.getPlatformPlayer().getConnection().getChannel().eventLoop(), sendDelay);
			}else {
				return new LegacyMessageController(protocol, handler, null, 0);
			}
		}
	}

	private static ServerMessageHandler createHandler(int ver, EaglerPlayerInstance<?> instance) {
		switch(ver) {
		case 4:
			return new ServerV4MessageHandler(instance);
		case 3:
			return new ServerV3MessageHandler(instance);
		default:
			throw new IllegalStateException();
		}
	}

}
