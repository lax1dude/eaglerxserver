package net.lax1dude.eaglercraft.backend.server.base.rpc;

public class VanillaPlayerRPCContext<PlayerObject> extends BasePlayerRPCContext<PlayerObject> {

	protected final BasePlayerRPCManager<PlayerObject> manager;

	VanillaPlayerRPCContext(BasePlayerRPCManager<PlayerObject> manager) {
		this.manager = manager;
	}

	@Override
	protected BasePlayerRPCManager<PlayerObject> manager() {
		return manager;
	}

}
