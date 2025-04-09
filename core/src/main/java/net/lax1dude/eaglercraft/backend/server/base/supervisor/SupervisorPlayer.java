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

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformPlayer;
import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformServer;
import net.lax1dude.eaglercraft.backend.server.base.BasePlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader.KeyedConsumerList;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetClientBrandUUID;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetOtherCape;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetOtherSkin;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketInvalidatePlayerCacheV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.util.SkinPacketVersionCache;

public class SupervisorPlayer {

	private final SupervisorService<?> controller;
	private final UUID playerUUID;

	private volatile int nodeId = -1;

	private volatile UUID brandUUID = null;
	private KeyedConsumerList<UUID, UUID> waitingBrandCallbacks = null;

	private volatile SkinPacketVersionCache skin = null;
	private final Object skinLock = new Object();
	private KeyedConsumerList<UUID, SkinPacketVersionCache> waitingSkinCallbacks = null;

	private volatile GameMessagePacket cape = null;
	private final Object capeLock = new Object();
	private KeyedConsumerList<UUID, GameMessagePacket> waitingCapeCallbacks = null;

	public SupervisorPlayer(SupervisorService<?> controller, UUID playerUUID) {
		this.controller = controller;
		this.playerUUID = playerUUID;
	}

	public SupervisorService<?> getController() {
		return controller;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void loadBrandUUID(UUID requester, Consumer<UUID> callback) {
		UUID val = brandUUID;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(this) {
				val = brandUUID;
				if(val != null) {
					break eag;
				}
				if(waitingBrandCallbacks == null) {
					waitingBrandCallbacks = new KeyedConsumerList<>();
					waitingBrandCallbacks.add(requester, callback);
				}else {
					waitingBrandCallbacks.add(requester, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			SupervisorConnection handler = controller.getConnection();
			if(handler != null) {
				handler.sendSupervisorPacket(new CPacketSvGetClientBrandUUID(playerUUID));
			}
		}
	}

	public void loadSkinData(UUID requester, Consumer<SkinPacketVersionCache> callback) {
		SkinPacketVersionCache val = skin;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(skinLock) {
				val = skin;
				if(val != null) {
					break eag;
				}
				if(waitingSkinCallbacks == null) {
					waitingSkinCallbacks = new KeyedConsumerList<>();
					waitingSkinCallbacks.add(requester, callback);
				}else {
					waitingSkinCallbacks.add(requester, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			SupervisorConnection handler = controller.getConnection();
			if(handler != null) {
				handler.sendSupervisorPacket(new CPacketSvGetOtherSkin(playerUUID));
			}
		}
	}

	public void loadCapeData(UUID requester, Consumer<GameMessagePacket> callback) {
		GameMessagePacket val = cape;
		if(val != null) {
			callback.accept(val);
		}else {
			eag: synchronized(capeLock) {
				val = cape;
				if(val != null) {
					break eag;
				}
				if(waitingCapeCallbacks == null) {
					waitingCapeCallbacks = new KeyedConsumerList<>();
					waitingCapeCallbacks.add(requester, callback);
				}else {
					waitingCapeCallbacks.add(requester, callback);
					return;
				}
			}
			if(val != null) {
				callback.accept(val);
				return;
			}
			SupervisorConnection handler = controller.getConnection();
			if(handler != null) {
				handler.sendSupervisorPacket(new CPacketSvGetOtherCape(playerUUID));
			}
		}
	}

	void onSkinReceived(SkinPacketVersionCache skin) {
		KeyedConsumerList<UUID, SkinPacketVersionCache> toCall;
		synchronized(skinLock) {
			if(this.skin != null) {
				return; // ignore multiple results
			}
			this.skin = skin;
			toCall = waitingSkinCallbacks;
			waitingSkinCallbacks = null;
		}
		if(toCall != null) {
			List<Consumer<SkinPacketVersionCache>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(skin);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onSkinError() {
		if(nodeId == -1) {
			controller.onDropPlayer(playerUUID);
		}else {
			KeyedConsumerList<UUID, SkinPacketVersionCache> toCall;
			synchronized(skinLock) {
				if(this.skin != null) {
					return; // ignore multiple results
				}
				toCall = waitingSkinCallbacks;
				waitingSkinCallbacks = null;
			}
			if(toCall != null) {
				List<Consumer<SkinPacketVersionCache>> toCallList = toCall.getList();
				for(int i = 0, l = toCallList.size(); i < l; ++i) {
					try {
						toCallList.get(i).accept(null);
					}catch(Exception ex) {
						controller.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void onCapeReceived(GameMessagePacket cape) {
		KeyedConsumerList<UUID, GameMessagePacket> toCall;
		synchronized(capeLock) {
			if(this.cape != null) {
				return; // ignore multiple results
			}
			this.cape = cape;
			toCall = waitingCapeCallbacks;
			waitingCapeCallbacks = null;
		}
		if(toCall != null) {
			List<Consumer<GameMessagePacket>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(cape);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onCapeError() {
		if(nodeId == -1) {
			controller.onDropPlayer(playerUUID);
		}else {
			KeyedConsumerList<UUID, GameMessagePacket> toCall;
			synchronized(capeLock) {
				if(this.cape != null) {
					return; // ignore multiple results
				}
				toCall = waitingCapeCallbacks;
				waitingCapeCallbacks = null;
			}
			if(toCall != null) {
				List<Consumer<GameMessagePacket>> toCallList = toCall.getList();
				for(int i = 0, l = toCallList.size(); i < l; ++i) {
					try {
						toCallList.get(i).accept(null);
					}catch(Exception ex) {
						controller.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void onNodeIDReceived(int nodeId, UUID brandUUID) {
		this.nodeId = nodeId;
		KeyedConsumerList<UUID, UUID> toCall;
		synchronized(this) {
			if(this.brandUUID != null) {
				return; // ignore multiple results
			}
			this.brandUUID = brandUUID;
			toCall = waitingBrandCallbacks;
			waitingBrandCallbacks = null;
		}
		if(toCall != null) {
			List<Consumer<UUID>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(brandUUID);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onNodeIDError() {
		if(nodeId == -1) {
			controller.onDropPlayer(playerUUID);
		}else {
			KeyedConsumerList<UUID, UUID> toCall;
			synchronized(this) {
				if(this.brandUUID != null) {
					return; // ignore multiple results
				}
				toCall = waitingBrandCallbacks;
				waitingBrandCallbacks = null;
			}
			if(toCall != null) {
				List<Consumer<UUID>> toCallList = toCall.getList();
				for(int i = 0, l = toCallList.size(); i < l; ++i) {
					try {
						toCallList.get(i).accept(null);
					}catch(Exception ex) {
						controller.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void playerDropped() {
		KeyedConsumerList<UUID, UUID> toCallA;
		KeyedConsumerList<UUID, SkinPacketVersionCache> toCallB;
		KeyedConsumerList<UUID, GameMessagePacket> toCallC;
		synchronized(this) {
			toCallA = waitingBrandCallbacks;
			waitingBrandCallbacks = null;
		}
		if(toCallA != null) {
			List<Consumer<UUID>> toCallAList = toCallA.getList();
			for(int i = 0, l = toCallAList.size(); i < l; ++i) {
				try {
					toCallAList.get(i).accept(null);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
		synchronized(skinLock) {
			toCallB = waitingSkinCallbacks;
			waitingSkinCallbacks = null;
		}
		if(toCallB != null) {
			List<Consumer<SkinPacketVersionCache>> toCallBList = toCallB.getList();
			for(int i = 0, l = toCallBList.size(); i < l; ++i) {
				try {
					toCallBList.get(i).accept(null);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
		synchronized(capeLock) {
			toCallC = waitingCapeCallbacks;
			waitingCapeCallbacks = null;
		}
		if(toCallC != null) {
			List<Consumer<GameMessagePacket>> toCallCList = toCallC.getList();
			for(int i = 0, l = toCallCList.size(); i < l; ++i) {
				try {
					toCallCList.get(i).accept(null);
				}catch(Exception ex) {
					controller.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onDropPartial(String serverNotify, boolean skin, boolean cape) {
		if(skin) {
			this.skin = null;
		}
		if(cape) {
			this.cape = null;
		}
		if(serverNotify != null) {
			IPlatformServer<?> svr = controller.getEaglerXServer().getPlatform().getRegisteredServers().get(serverNotify);
			if(svr != null) {
				SPacketInvalidatePlayerCacheV4EAG pkt = new SPacketInvalidatePlayerCacheV4EAG(skin, cape,
						playerUUID.getMostSignificantBits(), playerUUID.getLeastSignificantBits());
				for(IPlatformPlayer<?> otherPlayer : svr.getAllPlayers()) {
					EaglerPlayerInstance<?> eagPlayer = otherPlayer.<BasePlayerInstance<?>>getPlayerAttachment().asEaglerPlayer();
					if(eagPlayer != null) {
						if(eagPlayer.getEaglerProtocol().ver >= 4) {
							eagPlayer.sendEaglerMessage(pkt);
						}
					}
				}
			}else {
				controller.logger().warn("Received skin change for unknown server: " + serverNotify);
			}
		}
	}

}