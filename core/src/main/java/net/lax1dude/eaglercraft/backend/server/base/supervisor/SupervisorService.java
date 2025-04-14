package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformTask;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.brand.IBrandRegistry;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorResolver;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.base.config.ConfigDataSupervisor;
import net.lax1dude.eaglercraft.backend.server.base.supervisor.rpc.SupervisorRPCHandler;
import net.lax1dude.eaglercraft.backend.server.util.Util;
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
	private final SupervisorTimeoutLoop timeoutLoop;
	private final SupervisorRPCHandler rpcHandler;
	final SupervisorResolver resolver;
	final SupervisorResolverAll resolverAll;
	private final boolean ignoreV2UUID;
	private int serviceStateTracker = 0;
	private SupervisorConnection currentConnection = null;

	private IPlatformTask pingTask;
	private IPlatformTask timeoutHandshakeTask;

	public SupervisorService(EaglerXServer<PlayerObject> server) {
		this.server = server;
		this.config = server.getConfig().getSupervisor();
		this.timeoutLoop = new SupervisorTimeoutLoop(server.getPlatform().getScheduler(), 250000000l);
		this.rpcHandler = new SupervisorRPCHandler(this);
		this.resolver = new SupervisorResolver(this);
		this.resolverAll = new SupervisorResolverAll(resolver, server);
		this.ignoreV2UUID = config.isSupervisorLookupIgnoreV2UUID();
	}

	@Override
	public IEaglerXServerAPI<PlayerObject> getServerAPI() {
		return server;
	}

	public EaglerXServer<PlayerObject> getEaglerXServer() {
		return server;
	}

	public final IPlatformLogger logger() {
		return server.logger();
	}

	public final SupervisorTimeoutLoop timeoutLoop() {
		return timeoutLoop;
	}

	@Override
	public boolean isSupervisorEnabled() {
		return true;
	}

	@Override
	public boolean isSupervisorConnected() {
		return (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this) != null;
	}

	@Override
	public SupervisorConnection getConnection() {
		return (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
	}

	@Override
	public int getNodeId() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		return conn != null ? conn.getNodeId() : -1;
	}

	@Override
	public int getPlayerTotal() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		return conn != null ? conn.getPlayerTotal() : server.getPlatform().getPlayerTotal();
	}

	@Override
	public int getPlayerMax() {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		return conn != null ? conn.getPlayerMax() : server.getPlatform().getPlayerMax();
	}

	@Override
	public SupervisorRPCHandler getRPCHandler() {
		return rpcHandler;
	}

	@Override
	public ISupervisorResolver getPlayerResolver() {
		return resolverAll;
	}

	@Override
	public ISupervisorResolverImpl getRemoteOnlyResolver() {
		return resolver;
	}

	@Override
	public void handleEnable() {
		initiateConnection();
		if(pingTask != null) {
			pingTask.cancel();
		}
		pingTask = server.getPlatform().getScheduler().executeAsyncRepeatingTask(() -> {
			SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
			if(conn != null) {
				conn.updatePing(Util.steadyTime());
			}
		}, 500l, 1000l);
		if(timeoutHandshakeTask != null) {
			timeoutHandshakeTask.cancel();
		}
		timeoutHandshakeTask = server.getPlatform().getScheduler().executeAsyncRepeatingTask(() -> {
			SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
			if(conn != null) {
				conn.expireHandshakes(Util.steadyTime());
			}
		}, 5000l, 5000l);
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
		onConnectionEnd();
		if(pingTask != null) {
			pingTask.cancel();
			pingTask = null;
		}
		if(timeoutHandshakeTask != null) {
			timeoutHandshakeTask.cancel();
			timeoutHandshakeTask = null;
		}
		timeoutLoop.cancelAll();
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
			state = (int)SERVICE_STATE_TRACKER_HANDLE.getOpaque(this);
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
			logger().info("Assigned node ID " + nodeId);
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
			state = (int)SERVICE_STATE_TRACKER_HANDLE.getOpaque(this);
			if(state <= 0) {
				return;
			}
		}while(!SERVICE_STATE_TRACKER_HANDLE.compareAndSet(this, state, 0));
		onConnectionEnd();
		server.logger().error("Connection to supervisor was lost! Attempting to reconnect...");
		server.getPlatform().getScheduler().executeAsyncDelayed(this::initiateConnection, 1000l);
	}

	private void onNewConnection(SupervisorPacketHandler handler, int nodeId) {
		SupervisorConnection newConnection = new SupervisorConnection(this, handler, nodeId);
		handler.setConnectionProtocol(EaglerSupervisorProtocol.V1);
		handler.setConnectionHandler(new SupervisorClientV1Handler(newConnection));
		handler.channelWrite(new CPacketSvProxyBrand(switch (server.getPlatform().getType()) {
		case BUNGEE -> CPacketSvProxyBrand.PROXY_TYPE_BUNGEE;
		case VELOCITY -> CPacketSvProxyBrand.PROXY_TYPE_VELOCITY;
		default -> CPacketSvProxyBrand.PROXY_TYPE_EAGLER_STANDALONE;
		}, server.getPlatform().getVersion(), CPacketSvProxyBrand.PLUGIN_TYPE_EAGLERXSERVER, server.getServerBrand(),
				server.getServerVersion()));
		for(BasePlayerInstance<PlayerObject> player : server.getAllPlayersInternal()) {
			EaglerPlayerInstance<PlayerObject> dat = player.asEaglerPlayer();
			newConnection.acceptPlayer(player.getUniqueId(),
					dat != null ? dat.getEaglerBrandUUID() : IBrandRegistry.BRAND_VANILLA,
					player.getMinecraftProtocol(), dat != null ? dat.getEaglerProtocol().ver : 0,
					player.getUsername(), (res) -> {
						if(res != EnumAcceptPlayer.ACCEPT) {
							logger().error("Could not reregister player '" + player.getUsername()
									+ "' with supervisor! Result: " + res.name());
							player.disconnect(server.componentBuilder().buildTextComponent()
									.text("Failed to reinitialize connection to supervisor").end());
						}
					});
		}
		CURRENT_CONNECTION_HANDLE.setRelease(this, newConnection);
		resolver.flushDeferred();
	}

	private void onConnectionEnd() {
		SupervisorConnection handler = (SupervisorConnection) CURRENT_CONNECTION_HANDLE.getAndSet(this, null);
		if(handler != null) {
			handler.onConnectionEnd();
		}
		resolver.onConnectionEnd();
	}

	@Override
	public void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback) {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		if(conn != null) {
			conn.acceptPlayer(playerUUID, brandUUID, gameProtocol, eaglerProtocol, username, callback);
		}else {
			try {
				callback.accept(EnumAcceptPlayer.SUPERVISOR_UNAVAILABLE);
			}catch(Exception ex) {
				logger().error("Caught exception from supervisor player accept callback", ex);
			}
		}
	}

	@Override
	public void dropOwnPlayer(UUID clientUUID) {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		if(conn != null) {
			conn.dropOwnPlayer(clientUUID);
		}
	}

	@Override
	public void notifySkinChange(UUID playerUUID, String serverName, boolean skin, boolean cape) {
		SupervisorConnection conn = (SupervisorConnection)CURRENT_CONNECTION_HANDLE.getAcquire(this);
		if(conn != null) {
			conn.notifySkinChange(playerUUID, serverName, skin, cape);
		}
	}

}
