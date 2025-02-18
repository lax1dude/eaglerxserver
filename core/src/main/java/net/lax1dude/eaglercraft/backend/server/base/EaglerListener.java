package net.lax1dude.eaglercraft.backend.server.base;

import java.net.SocketAddress;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerListenerInfo;

public class EaglerListener implements IEaglerListenerInfo {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public SocketAddress getAddress() {
		return null;
	}

	@Override
	public boolean isDualStack() {
		return false;
	}

}
