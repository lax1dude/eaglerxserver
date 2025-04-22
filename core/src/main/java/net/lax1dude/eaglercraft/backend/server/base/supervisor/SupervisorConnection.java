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

package net.lax1dude.eaglercraft.backend.server.base.supervisor;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.MapMaker;

import io.netty.channel.Channel;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.INettyChannel;
import net.lax1dude.eaglercraft.backend.server.api.supervisor.ISupervisorConnection;
import net.lax1dude.eaglercraft.backend.server.base.EaglerXServer;
import net.lax1dude.eaglercraft.backend.server.util.Util;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.netty.SupervisorPacketHandler;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.EaglerSupervisorPacket;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvDropPlayer;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvDropPlayerPartial;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvPing;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvProxyStatus;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvRegisterPlayer;

public class SupervisorConnection implements ISupervisorConnection, INettyChannel.NettyUnsafe {

	private static final VarHandle LAST_PING_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			LAST_PING_HANDLE = l.findVarHandle(SupervisorConnection.class, "lastPing", long.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private static class PendingHandshake {

		protected final Consumer<EnumAcceptPlayer> consumer;
		protected final long createdAt;

		protected PendingHandshake(Consumer<EnumAcceptPlayer> consumer, long createdAt) {
			this.consumer = consumer;
			this.createdAt = createdAt;
		}

	}

	final IPlatformLogger logger;
	final SupervisorService<?> service;
	final SupervisorPacketHandler handler;
	final SupervisorLookupHandler<?> lookupHandler;
	final ConcurrentMap<UUID, SupervisorPlayer> remotePlayers;
	final Function<UUID, SupervisorPlayer> playerLoader;
	final Map<UUID, PendingHandshake> pendingHandshakes;
	final Set<UUID> acceptedPlayers;
	final ConcurrentMap<Integer, Set<UUID>> nodeIdAssociations;
	final int nodeId;
	private long lastPing;
	private int proxyPing;
	private int playerTotal;
	private int playerMax;

	SupervisorConnection(SupervisorService<?> service, SupervisorPacketHandler handler, int nodeId) {
		this.logger = service.logger();
		this.service = service;
		this.handler = handler;
		this.nodeId = nodeId;
		this.lookupHandler = new SupervisorLookupHandler<>(service, this);
		this.remotePlayers = (new MapMaker()).initialCapacity(2048).concurrencyLevel(16).makeMap();
		this.playerLoader = (uuid) -> {
			return new SupervisorPlayer(this, uuid);
		};
		this.pendingHandshakes = new HashMap<>(256);
		this.acceptedPlayers = Collections.newSetFromMap((new MapMaker()).initialCapacity(1024).concurrencyLevel(8).makeMap());
		this.nodeIdAssociations = (new MapMaker()).initialCapacity(64).concurrencyLevel(16).makeMap();
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return handler.getChannel().remoteAddress();
	}

	@Override
	public int getProtocolVersion() {
		return handler.getConnectionProtocol().vers;
	}

	@Override
	public int getNodeId() {
		return nodeId;
	}

	@Override
	public long getPing() {
		return proxyPing;
	}

	public int getPlayerTotal() {
		return playerTotal;
	}

	public int getPlayerMax() {
		return playerMax;
	}

	@Override
	public NettyUnsafe netty() {
		return this;
	}

	@Override
	public Channel getChannel() {
		return handler.getChannel();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return handler.getChannel().remoteAddress();
	}

	public void sendSupervisorPacket(EaglerSupervisorPacket msg) {
		handler.channelWrite(msg);
	}

	SupervisorPlayer loadPlayer(UUID playerUUID) {
		return remotePlayers.computeIfAbsent(playerUUID, playerLoader);
	}

	SupervisorPlayer loadPlayerIfPresent(UUID playerUUID) {
		return remotePlayers.get(playerUUID);
	}

	void onPongPacket() {
		long result = (long)LAST_PING_HANDLE.getAndSet(this, 0l);
		if(result != 0l) {
			proxyPing = (int)(Util.steadyTime() - result);
		}
	}

	void onPlayerCount(int playerTotal, int playerMax) {
		this.playerTotal = playerTotal;
		this.playerMax = playerMax;
	}

	void updatePing(long millis) {
		if(LAST_PING_HANDLE.compareAndSet(this, 0l, millis)) {
			handler.channelWrite(new CPacketSvPing());
		}
		handler.channelWrite(new CPacketSvProxyStatus(System.currentTimeMillis(),
				service.getEaglerXServer().getPlatform().getPlayerMax()));
	}

	void expireHandshakes(long millis) {
		List<PendingHandshake> lst = null;
		synchronized(pendingHandshakes) {
			if(pendingHandshakes.isEmpty()) {
				return;
			}
			Iterator<PendingHandshake> itr = pendingHandshakes.values().iterator();
			while(itr.hasNext()) {
				PendingHandshake h = itr.next();
				if(millis - h.createdAt > 10000l) {
					itr.remove();
					if(lst == null) {
						lst = new ArrayList<>(4);
					}
					lst.add(h);
				}
			}
		}
		if(lst != null) {
			for(PendingHandshake c : lst) {
				safeAccept(c.consumer, EnumAcceptPlayer.SUPERVISOR_UNAVAILABLE);
			}
		}
	}

	void acceptPlayer(UUID playerUUID, UUID brandUUID, int gameProtocol, int eaglerProtocol, String username,
			Consumer<EnumAcceptPlayer> callback) {
		if(!handler.getChannel().isActive()) {
			safeAccept(callback, EnumAcceptPlayer.SUPERVISOR_UNAVAILABLE);
			return;
		}
		if(acceptedPlayers.contains(playerUUID)) {
			safeAccept(callback, EnumAcceptPlayer.REJECT_DUPLICATE_UUID);
			return;
		}
		eagler: {
			synchronized(pendingHandshakes) {
				if(!pendingHandshakes.containsKey(playerUUID)) {
					pendingHandshakes.put(playerUUID, new PendingHandshake(callback, Util.steadyTime()));
				}else {
					break eagler;
				}
			}
			sendSupervisorPacket(new CPacketSvRegisterPlayer(playerUUID, brandUUID, gameProtocol, eaglerProtocol,
					username.toLowerCase(Locale.US)));
			return;
		}
		safeAccept(callback, EnumAcceptPlayer.REJECT_ALREADY_WAITING);
	}

	void onPlayerAccept(UUID playerUUID, EnumAcceptPlayer reason) {
		PendingHandshake c;
		synchronized(pendingHandshakes) {
			c = pendingHandshakes.remove(playerUUID);
		}
		if(c != null) {
			if(reason == EnumAcceptPlayer.ACCEPT) {
				acceptedPlayers.add(playerUUID);
			}
			safeAccept(c.consumer, reason);
		}else {
			service.logger().warn("Received accept/reject signal for unknown player " + playerUUID);
		}
	}

	void setRemotePlayerNode(UUID playerUUID, int nodeId) {
		nodeIdAssociations.compute(nodeId, (k, v) -> {
			if(v == null) {
				v = new HashSet<>(1024);
			}
			v.add(playerUUID);
			return v;
		});
	}

	void dropPlayerFromNode(UUID playerUUID, int nodeId) {
		nodeIdAssociations.compute(nodeId, (k, v) -> {
			if(v != null) {
				if(v.remove(playerUUID) && v.isEmpty()) {
					v = null;
				}
			}
			return v;
		});
	}

	void onDropAllPlayers(int nodeId) {
		Set<UUID> set = nodeIdAssociations.remove(nodeId);
		if(set != null) {
			for(UUID uuid : set) {
				SupervisorPlayer player = remotePlayers.remove(uuid);
				if(player != null) {
					player.playerDropped();
				}
			}
		}
	}

	void onDropPlayer(UUID playerUUID) {
		SupervisorPlayer player = remotePlayers.remove(playerUUID);
		if(player != null) {
			int node = player.getNodeId();
			if(node != -1) {
				dropPlayerFromNode(playerUUID, node);
			}
			player.playerDropped();
		}
	}

	void dropOwnPlayer(UUID playerUUID) {
		remotePlayers.remove(playerUUID);
		if(acceptedPlayers.remove(playerUUID)) {
			sendSupervisorPacket(new CPacketSvDropPlayer(playerUUID));
		}
	}

	void notifySkinChange(UUID uuid, String serverName, boolean skin, boolean cape) {
		int mask = 0;
		if(skin) mask |= CPacketSvDropPlayerPartial.DROP_PLAYER_SKIN;
		if(cape) mask |= CPacketSvDropPlayerPartial.DROP_PLAYER_CAPE;
		if(mask > 0) {
			sendSupervisorPacket(new CPacketSvDropPlayerPartial(uuid, serverName, mask));
		}
	}

	void onConnectionEnd() {
		List<PendingHandshake> lst;
		synchronized(pendingHandshakes) {
			lst = new ArrayList<>(pendingHandshakes.values());
			pendingHandshakes.clear();
		}
		for(PendingHandshake c : lst) {
			safeAccept(c.consumer, EnumAcceptPlayer.SUPERVISOR_UNAVAILABLE);
		}
	}

	private void safeAccept(Consumer<EnumAcceptPlayer> consumer, EnumAcceptPlayer value) {
		try {
			consumer.accept(value);
		}catch(Exception ex) {
			service.logger().error("Caught exception from supervisor player accept callback", ex);
		}
	}

	IPlatformLogger logger() {
		return logger;
	}

	EaglerXServer<?> getEaglerXServer() {
		return service.getEaglerXServer();
	}

}
