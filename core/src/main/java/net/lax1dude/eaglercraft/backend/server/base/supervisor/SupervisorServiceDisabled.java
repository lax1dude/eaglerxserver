package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;

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
	public SupervisorRPCHandler getRPCHandler() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolver getPlayerResolver() {
		throw supervisorDisable();
	}

	@Override
	public ISupervisorResolverImpl getRemoteOnlyResolver() {
		throw supervisorDisable();
	}

	private static UnsupportedOperationException supervisorDisable() {
		return new UnsupportedOperationException("Supervisor is not enabled!");
	}

	@Override
	public void handleEnable() {
	}

	@Override
	public void handleDisable() {
	}

	@Override
	public boolean shouldIgnoreUUID(UUID uuid) {
		return true;
	}

	@Override
	public void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback) {
		throw supervisorDisable();
	}

}
