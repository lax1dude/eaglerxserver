package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;

public class SupervisorServiceDisabled<PlayerObject> implements ISupervisorServiceImpl<PlayerObject> {

	private final EaglerXServer<PlayerObject> server;

	public SupervisorServiceDisabled(EaglerXServer<PlayerObject> server) {
		this.server = server;
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	@Override
	public boolean isSupervisorEnabled() {
		return false;
	}

	@Override
	public boolean isSupervisorConnected() {
		return false;
	}

	@Override
	public ISupervisorConnection getConnection() {
		throw supervisorDisable();
	}

	@Override
	public int getNodeId() {
		return -1;
	}

	@Override
	public int getPlayerTotal() {
		return server.getPlatform().getPlayerTotal();
	}

	@Override
	public int getPlayerMax() {
		return server.getPlatform().getPlayerMax();
	}

	@Override
	public ISupervisorRPCHandler getRPCHandler() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolver getPlayerResolver() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolver getRemoteOnlyResolver() {
		throw supervisorDisable();
	}

	private static UnsupportedOperationException supervisorDisable() {
		return new UnsupportedOperationException("Supervisor is not enabled!");
	}

}
