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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private final int nodeId;
	private final EaglerXSupervisorServer server;
	private final SupervisorPacketHandler handler;

	private final ReadWriteLock playersLock = new ReentrantReadWriteLock();
	private final Map<UUID, SupervisorPlayerInstance> players = new HashMap<>(1024);

	private EnumProxyType proxyType = EnumProxyType.UNKNOWN;
	private String proxyVersion = "Unknown";
	private EnumPluginType pluginType = EnumPluginType.UNKNOWN;
	private String pluginBrand = "Unknown";
	private String pluginVersion = "Unknown";

	private long proxySystemTime;
	private int playerMax;

	private final AtomicLong proxyPingSentTime = new AtomicLong(0l);
	private int proxyPing;

	private long lastRPCFlush = SteadyTime.millis();
	private final ConcurrentMap<UUID, PendingRPC> pendingRPC = new ConcurrentHashMap<>();

	public static abstract class PendingRPC {

		public static final int FAILURE_PROCEDURE = 0;
		public static final int FAILURE_TIMEOUT = 1;
		public static final int FAILURE_HANGUP = 2;

		protected final long timeout;

		protected PendingRPC(long timeout) {
			this.timeout = timeout;
		}

		protected abstract void onSuccess(byte[] dataBuffer);

		protected abstract void onFailure(int type);

	}

	public SupervisorClientInstance(int nodeId, EaglerXSupervisorServer server, SupervisorPacketHandler handler) {
		this.nodeId = nodeId;
		this.server = server;
		this.handler = handler;
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
		return proxySystemTime;
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
		return proxyPing;
	}

	public int getPlayerMax() {
		return playerMax;
	}

	public void handleDisconnected() {
		List<UUID> expired = new ArrayList<>(pendingRPC.size());
		pendingRPC.forEach((uuid, proc) -> {
			expired.add(uuid);
		});
		for(int i = 0, l = expired.size(); i < l; ++i) {
			PendingRPC cb = pendingRPC.remove(expired.get(i));
			if(cb != null) {
				cb.onFailure(PendingRPC.FAILURE_HANGUP);
			}
		}
		pendingRPC.clear();
		server.unregisterClient(this);
	}

	public void update() {
		long millis = SteadyTime.millis();
		if(proxyPingSentTime.compareAndSet(0l, millis)) {
			handler.channelWrite(new SPacketSvPing());
		}
		if(millis - lastRPCFlush > 2500l) {
			lastRPCFlush = millis;
			List<UUID> expired = new ArrayList<>(4);
			pendingRPC.forEach((uuid, proc) -> {
				if(proc.timeout < millis) {
					expired.add(uuid);
				}
			});
			for(int i = 0, l = expired.size(); i < l; ++i) {
				PendingRPC cb = pendingRPC.remove(expired.get(i));
				if(cb != null) {
					cb.onFailure(PendingRPC.FAILURE_TIMEOUT);
				}
			}
		}
	}

	public void onPongPacket() {
		long result = proxyPingSentTime.getAndSet(0l);
		if(result != 0l) {
			proxyPing = (int)(SteadyTime.millis() - result);
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
		this.proxySystemTime = proxySystemTime;
		if(this.playerMax != playerMax) {
			this.playerMax = playerMax;
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

	public void invokeRPC(UUID requestUUID, int sourceNodeId, String procName, byte[] dataBuffer, PendingRPC callback) {
		pendingRPC.put(requestUUID, callback);
		handler.channelWrite(new SPacketSvRPCExecute(requestUUID, sourceNodeId, procName.getBytes(StandardCharsets.US_ASCII), dataBuffer));
	}

	public void invokeRPC(UUID requestUUID, int sourceNodeId, byte[] procName, byte[] dataBuffer, PendingRPC callback) {
		pendingRPC.put(requestUUID, callback);
		handler.channelWrite(new SPacketSvRPCExecute(requestUUID, sourceNodeId, procName, dataBuffer));
	}

	public void invokeRPCVoid(int sourceNodeId, String procName, byte[] dataBuffer) {
		handler.channelWrite(new SPacketSvRPCExecuteVoid(sourceNodeId, procName.getBytes(StandardCharsets.US_ASCII), dataBuffer));
	}

	public void invokeRPCVoid(int sourceNodeId, byte[] procName, byte[] dataBuffer) {
		handler.channelWrite(new SPacketSvRPCExecuteVoid(sourceNodeId, procName, dataBuffer));
	}

	void onRPCResultSuccess(UUID requestUUID, byte[] dataBuffer) {
		PendingRPC pending = pendingRPC.remove(requestUUID);
		if(pending != null) {
			pending.onSuccess(dataBuffer);
		}else {
			logger.warn("Received RPC success for unknown/expired request {}", requestUUID);
		}
	}

	void onRPCResultFail(UUID requestUUID) {
		PendingRPC pending = pendingRPC.remove(requestUUID);
		if(pending != null) {
			pending.onFailure(PendingRPC.FAILURE_PROCEDURE);
		}else {
			logger.warn("Received RPC failure for unknown/expired request {}", requestUUID);
		}
	}

}