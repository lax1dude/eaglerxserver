package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;

public class EaglerPlayerRPCManager<PlayerObject> extends BasePlayerRPCManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;

	EaglerPlayerRPCManager(EaglerPlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	@Override
	public EaglerPlayerInstance<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return true;
	}

}
