package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageChannel;
import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerMessageHandler;

public class MessageChannel<PlayerObject> implements IEaglerXServerMessageChannel<PlayerObject> {

	private final String legacyName;
	private final String modernName;
	private final IEaglerXServerMessageHandler<PlayerObject> handler;

	public MessageChannel(String legacyName, String modernName, IEaglerXServerMessageHandler<PlayerObject> handler) {
		this.legacyName = legacyName;
		this.modernName = modernName;
		this.handler = handler;
	}

	@Override
	public String getLegacyName() {
		return legacyName;
	}

	@Override
	public String getModernName() {
		return modernName;
	}

	@Override
	public IEaglerXServerMessageHandler<PlayerObject> getHandler() {
		return handler;
	}

}
