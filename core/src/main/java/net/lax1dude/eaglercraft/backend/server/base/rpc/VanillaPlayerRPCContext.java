package net.lax1dude.eaglercraft.backend.server.base.rpc;

import net.lax1dude.eaglercraft.backend.rpc.protocol.EaglerBackendRPCProtocol;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;

public class VanillaPlayerRPCContext<PlayerObject> extends BasePlayerRPCContext<PlayerObject> {

	protected final BasePlayerRPCManager<PlayerObject> manager;

	VanillaPlayerRPCContext(BasePlayerRPCManager<PlayerObject> manager, EaglerBackendRPCProtocol protocol) {
		super(protocol);
		this.manager = manager;
	}

	@Override
	protected BasePlayerRPCManager<PlayerObject> manager() {
		return manager;
	}

	@Override
	protected IPlatformLogger logger() {
		return manager.getPlayer().getEaglerXServer().logger();
	}

}
