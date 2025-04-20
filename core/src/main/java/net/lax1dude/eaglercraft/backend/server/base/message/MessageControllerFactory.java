package net.lax1dude.eaglercraft.backend.server.base.message;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;

public class MessageControllerFactory {

	public static MessageController initializePlayer(EaglerPlayerInstance<?> instance) {
		GamePluginMessageProtocol protocol = instance.getEaglerProtocol();
		ServerMessageHandler handler = createHandler(protocol.ver, instance);
		RewindMessageControllerHandle rewindHandle = instance.connectionImpl().getRewindMessageControllerHandle();
		if(rewindHandle != null) {
			return new RewindMessageControllerImpl(rewindHandle, protocol, handler);
		}
		EaglerXServer<?> server = instance.getEaglerXServer();
		int sendDelay = server.getConfig().getSettings().getProtocolV4DefragSendDelay();
		if (protocol.ver >= 5) {
			return InjectedMessageController.injectEagler(protocol, handler,
					instance.getPlatformPlayer().getConnection().getChannel(), sendDelay);
		}else {
			boolean modernChannelNames = server.getPlatform().isModernPluginChannelNamesOnly()
					|| instance.getMinecraftProtocol() > 340;
			if (protocol.ver == 4 && sendDelay > 0) {
				return new LegacyMessageController(protocol, handler,
						instance.getPlatformPlayer().getConnection().getChannel().eventLoop(), sendDelay, modernChannelNames);
			}else {
				return new LegacyMessageController(protocol, handler, null, 0, modernChannelNames);
			}
		}
	}

	private static ServerMessageHandler createHandler(int ver, EaglerPlayerInstance<?> instance) {
		return switch(ver) {
		case 5 -> new ServerV5MessageHandler(instance);
		case 4 -> new ServerV4MessageHandler(instance);
		case 3 -> new ServerV3MessageHandler(instance);
		default -> throw new IllegalStateException();
		};
	}

}
