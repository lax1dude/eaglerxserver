package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;

public class VanillaPlayerRPCManager<PlayerObject> extends BasePlayerRPCManager<PlayerObject> {

	private final BasePlayerInstance<PlayerObject> player;

	VanillaPlayerRPCManager(BasePlayerInstance<PlayerObject> player) {
		this.player = player;
	}

	@Override
	public BasePlayerInstance<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public boolean isEaglerPlayer() {
		return false;
	}

}
