package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSupervisor;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.EaglerSupervisorProtocol;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvHandshake;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvProxyBrand;

public class SupervisorService<PlayerObject> implements ISupervisorServiceImpl<PlayerObject> {

	private static final VarHandle SERVICE_STATE_TRACKER_HANDLE;
	private static final VarHandle CURRENT_CONNECTION_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			SERVICE_STATE_TRACKER_HANDLE = l.findVarHandle(SupervisorService.class, "serviceStateTracker", int.class);
			CURRENT_CONNECTION_HANDLE = l.findVarHandle(SupervisorService.class, "currentConnection", SupervisorConnection.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final EaglerXServer<PlayerObject> server;
	private final ConfigDataSupervisor config;
	private final boolean ignoreV2UUID;
	final SupervisorResolver<PlayerObject> resolver;
	private int serviceStateTracker = 0;
	private SupervisorConnection currentConnection = null;

	public SupervisorService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.config = server.getConfig().getSupervisor();
		this.ignoreV2UUID = config.isSupervisorLookupIgnoreV2UUID();
		this.resolver = new SupervisorResolver<>(this);
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public EaglerXServer<PlayerObject> getEaglerXServer() {
		return server;
	}

	final IPlatformLogger logger() {
		return server.logger();
	}

	@Override
	public boolean isSupervisorEnabled() {
		return true;
	}

	@Override
	public boolean isSupervisorConnected() {
		return (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getOpaque(this) != null;
	}

	@Override
	public SupervisorConnection getConnection() {
		return (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getOpaque(this);
	}

	@Override
	public int getNodeId() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getOpaque(this);
		return conn != null ? conn.getNodeId() : -1;
	}

	@Override
	public int getPlayerTotal() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getOpaque(this);
		return conn != null ? conn.getPlayerTotal() : server.getPlatform().getPlayerTotal();
	}

	@Override
	public int getPlayerMax() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getOpaque(this);
		return conn != null ? conn.getPlayerMax() : server.getPlatform().getPlayerMax();
	}

	@Override
	public ISupervisorRPCHandler getRPCHandler() {
		return null; // TODO
	}

	@Override
	public ISupervisorResolver getPlayerResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISupervisorResolverImpl getRemoteOnlyResolver() {
		return resolver;
	}

	@Override
	public void handleEnable() {
		initiateConnection();
	}

	@Override
	public void handleDisable() {
		server.logger().info("Attempting to terminate supervisor client");
		SERVICE_STATE_TRACKER_HANDLE.setVolatile(this, -1);
		SupervisorConnection handler = (SupervisorConnection) CURRENT_CONNECTION_HANDLE.getAndSet(this, null);
		if(handler != null) {
			try {
				handler.getChannel().close().await();
			} catch (InterruptedException e) {
			}
		}
		onConnectionEnd(true);
	}

	@Override
	public boolean shouldIgnoreUUID(UUID uuid) {
		return ignoreV2UUID && uuid.version() == 2;
	}

	public void initiateConnection() {
		if(SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, 0, 1)) {
			PipelineFactory.initiateConnection(server, config.getSupervisorAddress(), this,
					config.getSupervisorConnectTimeout(), config.getSupervisorReadTimeout());
		}
	}

	void handleChannelOpen(SupervisorPacketHandler h) {
		if(SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, 1, 2)) {
			logger().info("Channel to supervisor opened");
			h.channelWrite(new CPacketSvHandshake(new int[] { EaglerSupervisorProtocol.V1.vers },
					config.getSupervisorSecret()));
		}else {
			logger().error("Unexpected supervisor channel open");
			h.getChannel().close();
		}
	}

	void handleChannelFailure() {
		int state;
		do {
			state = (int)SERVICE_STATE_TRACKER_HANDLE.getOpaque();
			if(state <= 0) {
				return;
			}
		}while(!SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, state, 0));
		logger().error("Failed to open supervisor channel! Retrying...");
		server.getPlatform().getScheduler().executeAsyncDelayed(this::initiateConnection, 1000l);
	}

	void handleHandshakeSuccess(SupervisorPacketHandler h, int nodeId) {
		if(SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, 2, 3)) {
			logger().info("Supervisor handshake successful");
			onNewConnection(h, nodeId);
		}else {
			logger().error("Unexpected supervisor handshake success");
			h.getChannel().close();
		}
	}

	void handleHandshakeFailure(SupervisorPacketHandler h, String failureCode) {
		logger().error("Supervisor handshake failed, reason: " + failureCode);
		h.getChannel().close();
	}

	void handleDisconnected() {
		int state;
		do {
			state = (int)SERVICE_STATE_TRACKER_HANDLE.getOpaque();
			if(state <= 0) {
				return;
			}
		}while(!SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, state, 0));
		onConnectionEnd(false);
		server.logger().error("Connection to supervisor was lost! Attempting to reconnect...");
		server.getPlatform().getScheduler().executeAsyncDelayed(this::initiateConnection, 1000l);
	}

	private void onNewConnection(SupervisorPacketHandler handler, int nodeId) {
		handler.channelWrite(new CPacketSvProxyBrand(switch (server.getPlatform().getType()) {
		case BUNGEE -> CPacketSvProxyBrand.PROXY_TYPE_BUNGEE;
		case VELOCITY -> CPacketSvProxyBrand.PROXY_TYPE_VELOCITY;
		default -> CPacketSvProxyBrand.PROXY_TYPE_EAGLER_STANDALONE;
		}, server.getPlatform().getVersion(), CPacketSvProxyBrand.PLUGIN_TYPE_EAGLERXSERVER, server.getServerBrand(),
				server.getServerVersion()));
		//TODO: register players
		CURRENT_CONNECTION_HANDLE.setOpaque(this, new SupervisorConnection(this, handler, nodeId));
	}

	private void onConnectionEnd(boolean shuttingDown) {
		CURRENT_CONNECTION_HANDLE.setOpaque(this, null);
		//TODO: unregister players
	}

	SupervisorPlayer loadPlayer(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	SupervisorPlayer loadPlayerIfPresent(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	void onPlayerAccept(UUID playerUUID, EnumAcceptPlayer reason) {
		//TODO
	}

	public void dropOwnPlayer(UUID clientUUID) {
		//TODO
	}

	void setRemotePlayerNode(UUID playerUUID, int nodeId) {
		//TODO
	}

	void dropPlayerFromNode(UUID playerUUID, int nodeId) {
		//TODO
	}

	void onDropAllPlayers(int nodeId) {
		//TODO
	}

	void onDropPlayer(UUID playerUUID) {
		//TODO
	}

}
