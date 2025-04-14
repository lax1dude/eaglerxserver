/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.backend.supervisor.server;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import net.lax1dude.eaglercraft.backend.supervisor.EaglerXSupervisorServer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvPing;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCExecute;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.server.SPacketSvRPCExecuteVoid;
import net.lax1dude.eaglercraft.backend.supervisor.server.player.SupervisorPlayerInstance;
import net.lax1dude.eaglercraft.backend.supervisor.util.AlreadyRegisteredException;
import net.lax1dude.eaglercraft.backend.supervisor.util.EnumPluginType;
import net.lax1dude.eaglercraft.backend.supervisor.util.EnumProxyType;
import net.lax1dude.eaglercraft.backend.util.SteadyTime;

public class SupervisorClientInstance {

	private static final Logger logger = LoggerFactory.getLogger("SupervisorClientInstance");

	private static final VarHandle PING_SENT_TIME_HANDLE;
	private static final VarHandle PING_VALUE_HANDLE;
	private static final VarHandle PLAYER_MAX_VALUE_HANDLE;
	private static final VarHandle SYS_TIME_VALUE_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			PING_SENT_TIME_HANDLE = l.findVarHandle(SupervisorClientInstance.class, "proxyPingSentTime", long.class);
			PING_VALUE_HANDLE = l.findVarHandle(SupervisorClientInstance.class, "proxyPing", int.class);
			PLAYER_MAX_VALUE_HANDLE = l.findVarHandle(SupervisorClientInstance.class, "playerMax", int.class);
			SYS_TIME_VALUE_HANDLE = l.findVarHandle(SupervisorClientInstance.class, "proxySystemTime", long.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final int nodeId;
	private final EaglerXSupervisorServer server;
	private final SupervisorPacketHandler handler;
	private final TimeoutLoop timeout;
	private volatile int disposed = 0;

	private final ReadWriteLock playersLock = new ReentrantReadWriteLock();
	private final Map<UUID, SupervisorPlayerInstance> players = new HashMap<>(1024);

	private EnumProxyType proxyType = EnumProxyType.UNKNOWN;
	private String proxyVersion = "Unknown";
	private EnumPluginType pluginType = EnumPluginType.UNKNOWN;
	private String pluginBrand = "Unknown";
	private String pluginVersion = "Unknown";

	private volatile long proxySystemTime;
	private volatile int playerMax;

	private volatile long proxyPingSentTime = 0l;
	private volatile int proxyPing;

	private final Map<UUID, RPCPending> pendingRPC = new HashMap<>(256);

	private Set<RPCMultiResultAggregator> multiResultAggregators = new HashSet<>(32);

	public SupervisorClientInstance(int nodeId, EaglerXSupervisorServer server, SupervisorPacketHandler handler) {
		this.nodeId = nodeId;
		this.server = server;
		this.handler = handler;
		this.timeout = new TimeoutLoop(handler.getChannel().eventLoop(), 250l * 1000000l);
	}

	public int getNodeId() {
		return nodeId;
	}

	public EaglerXSupervisorServer getServer() {
		return server;
	}

	public SupervisorPacketHandler getHandler() {
		return handler;
	}

	public void sendPacket(EaglerSupervisorPacket packet) {
		handler.channelWrite(packet);
	}

	public EnumProxyType getProxyType() {
		return proxyType;
	}

	public String getProxyVersion() {
		return proxyVersion;
	}

	public EnumPluginType getPluginType() {
		return pluginType;
	}

	public String getPluginBrand() {
		return pluginBrand;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public long getProxySystemTime() {
		return (long)SYS_TIME_VALUE_HANDLE.getOpaque(this);
	}

	public int getPlayerCount() {
		playersLock.readLock().lock();
		try {
			return players.size();
		}finally {
			playersLock.readLock().unlock();
		}
	}

	public int getProxyPing() {
		return (int)PING_VALUE_HANDLE.getOpaque(this);
	}

	public int getPlayerMax() {
		return (int)PLAYER_MAX_VALUE_HANDLE.getOpaque(this);
	}

	public void handleDisconnected() {
		try {
			List<UUID> expired = new ArrayList<>(pendingRPC.size());
			pendingRPC.forEach((uuid, proc) -> {
				expired.add(uuid);
			});
			for(int i = 0, l = expired.size(); i < l; ++i) {
				RPCPending cb = pendingRPC.remove(expired.get(i));
				if(cb != null) {
					cb.onFailure(RPCPending.FAILURE_HANGUP);
				}
			}
		}finally {
			try {
				if(multiResultAggregators != null) {
					for(RPCMultiResultAggregator aggregator : multiResultAggregators) {
						aggregator.destroy();
					}
				}
			}finally {
				server.unregisterClient(this);
			}
		}
	}

	public void update() {
		long millis = SteadyTime.millis();
		if(PING_SENT_TIME_HANDLE.compareAndSet(this, 0l, millis)) {
			handler.channelWrite(new SPacketSvPing());
		}
	}

	public void onPongPacket() {
		long result = (long)PING_SENT_TIME_HANDLE.getAndSet(this, 0l);
		if(result != 0l) {
			PING_VALUE_HANDLE.setOpaque(this, (int)(SteadyTime.millis() - result));
		}
	}

	public void onProxyBrandPacket(EnumProxyType proxyType, String proxyVersion, EnumPluginType pluginType,
			String pluginBrand, String pluginVersion) {
		this.proxyType = proxyType;
		this.proxyVersion = proxyVersion;
		this.pluginType = pluginType;
		this.pluginBrand = pluginBrand;
		this.pluginVersion = pluginVersion;
	}

	public void onProxyStatusPacket(long proxySystemTime, int playerMax) {
		SYS_TIME_VALUE_HANDLE.setOpaque(this, proxySystemTime);
		if((int)PLAYER_MAX_VALUE_HANDLE.getAndSet(this, playerMax) != playerMax) {
			this.server.recalcMaxPlayers();
		}
	}

	public void registerProxyPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username) throws AlreadyRegisteredException {
		SupervisorPlayerInstance instance = server.registerPlayer(this, playerUUID, brandUUID, gameProtocol, eaglerProtocol, username);
		playersLock.writeLock().lock();
		try {
			players.put(playerUUID, instance);
		}finally {
			playersLock.writeLock().unlock();
		}
	}

	public void dropProxyPlayer(UUID playerUUID) {
		playersLock.writeLock().lock();
		try {
			players.remove(playerUUID);
		}finally {
			playersLock.writeLock().unlock();
		}
		server.unregisterPlayer(playerUUID);
	}

	public SupervisorPlayerInstance getPlayerByUUID(UUID uuid) {
		playersLock.readLock().lock();
		try {
			return players.get(uuid);
		}finally {
			playersLock.readLock().unlock();
		}
	}

	public void invokeRPC(int sourceNodeId, int procNameLen, ByteBuf dataBuffer, RPCPending callback) {
		dataBuffer.retain();
		handler.getChannel().eventLoop().execute(() -> {
			try {
				callback.map = pendingRPC;
				pendingRPC.put(callback.key, callback);
				if(timeout.addFuture(callback)) {
					handler.channelWrite(
							new SPacketSvRPCExecute(callback.key, sourceNodeId, procNameLen, dataBuffer.retain()));
				}
			}finally {
				dataBuffer.release();
			}
		});
	}

	public void invokeRPCVoid(int sourceNodeId, int procNameLen, ByteBuf dataBuffer) {
		handler.channelWrite(new SPacketSvRPCExecuteVoid(sourceNodeId, procNameLen, dataBuffer.retain()));
	}

	public boolean addPendingResultAggregator(RPCMultiResultAggregator aggregator) {
		if(multiResultAggregators != null) {
			aggregator.set = multiResultAggregators;
			aggregator.eventLoop = handler.getChannel().eventLoop();
			multiResultAggregators.add(aggregator);
			return true;
		}else {
			logger.error("Refusing to add result aggregator to client, connection is already disposed");
			return false;
		}
	}

	void onRPCResultSuccess(UUID requestUUID, ByteBuf dataBuffer) {
		RPCPending pending = pendingRPC.remove(requestUUID);
		if(pending != null) {
			pending.onSuccess(dataBuffer);
		}else {
			logger.warn("Received RPC success for unknown/expired request {}", requestUUID);
		}
	}

	void onRPCResultFail(UUID requestUUID) {
		RPCPending pending = pendingRPC.remove(requestUUID);
		if(pending != null) {
			pending.onFailure(RPCPending.FAILURE_PROCEDURE);
		}else {
			logger.warn("Received RPC failure for unknown/expired request {}", requestUUID);
		}
	}

}