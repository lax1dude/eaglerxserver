package net.lax1dude.eaglercraft.backend.server.base;

import net.lax1dude.eaglercraft.backend.server.adapter.IEaglerXServerJoinListener;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;

class EaglerXServerJoinListener<PlayerObject> implements IEaglerXServerJoinListener<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	EaglerXServerJoinListener(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public void handlePreConnect(IPlatformPlayer<PlayerObject> player) {
		server.handleServerPreConnect(player.<BasePlayerInstance<PlayerObject>>getPlayerAttachment());
	}

	@Override
	public void handlePostConnect(IPlatformPlayer<PlayerObject> player, IPlatformServer<PlayerObject> serverIn) {
		server.handleServerPostConnect(player.<BasePlayerInstance<PlayerObject>>getPlayerAttachment(), serverIn);
	}

}
