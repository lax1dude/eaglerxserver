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
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerCape;
import net.lax1dude.eaglercraft.backend.server.api.skins.IEaglerPlayerSkin;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingCape;
import net.lax1dude.eaglercraft.backend.server.base.skins.type.MissingSkin;
import net.lax1dude.eaglercraft.backend.server.util.KeyedConcurrentLazyLoader.KeyedConsumerList;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetClientBrandUUID;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetOtherCape;
import net.lax1dude.eaglercraft.backend.supervisor.protocol.pkt.client.CPacketSvGetOtherSkin;

class SupervisorPlayer {

	private static final VarHandle NODE_ID_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			NODE_ID_HANDLE = l.findVarHandle(SupervisorPlayer.class, "nodeId", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final SupervisorConnection connection;
	private final UUID playerUUID;

	private volatile int nodeId = -1;

	private UUID brandUUID = null;
	private KeyedConsumerList<UUID, UUID> waitingBrandCallbacks = null;

	private IEaglerPlayerSkin skin = null;
	private final Object skinLock = new Object();
	private KeyedConsumerList<UUID, IEaglerPlayerSkin> waitingSkinCallbacks = null;

	private IEaglerPlayerCape cape = null;
	private final Object capeLock = new Object();
	private KeyedConsumerList<UUID, IEaglerPlayerCape> waitingCapeCallbacks = null;

	SupervisorPlayer(SupervisorConnection connection, UUID playerUUID) {
		this.connection = connection;
		this.playerUUID = playerUUID;
	}

	public SupervisorConnection getConnection() {
		return connection;
	}

	public SupervisorService<?> getController() {
		return connection.service;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public int getNodeId() {
		return (int)NODE_ID_HANDLE.getOpaque(this);
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
			connection.sendSupervisorPacket(new CPacketSvGetClientBrandUUID(playerUUID));
		}
	}

	public void loadSkinData(UUID requester, Consumer<IEaglerPlayerSkin> callback) {
		IEaglerPlayerSkin val = skin;
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
			connection.sendSupervisorPacket(new CPacketSvGetOtherSkin(playerUUID));
		}
	}

	public void loadCapeData(UUID requester, Consumer<IEaglerPlayerCape> callback) {
		IEaglerPlayerCape val = cape;
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
			connection.sendSupervisorPacket(new CPacketSvGetOtherCape(playerUUID));
		}
	}

	void onSkinReceived(IEaglerPlayerSkin skin) {
		KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall;
		synchronized(skinLock) {
			if(this.skin != null) {
				return; // ignore multiple results
			}
			this.skin = skin;
			toCall = waitingSkinCallbacks;
			waitingSkinCallbacks = null;
		}
		if(toCall != null) {
			List<Consumer<IEaglerPlayerSkin>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(skin);
				}catch(Exception ex) {
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onSkinError() {
		if((int)NODE_ID_HANDLE.getOpaque(this) == -1) {
			connection.onDropPlayer(playerUUID);
		}else {
			KeyedConsumerList<UUID, IEaglerPlayerSkin> toCall;
			synchronized(skinLock) {
				if(this.skin != null) {
					return; // ignore multiple results
				}
				toCall = waitingSkinCallbacks;
				waitingSkinCallbacks = null;
			}
			if(toCall != null) {
				List<Consumer<IEaglerPlayerSkin>> toCallList = toCall.getList();
				for(int i = 0, l = toCallList.size(); i < l; ++i) {
					try {
						toCallList.get(i).accept(MissingSkin.MISSING_SKIN);
					}catch(Exception ex) {
						connection.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void onCapeReceived(IEaglerPlayerCape cape) {
		KeyedConsumerList<UUID, IEaglerPlayerCape> toCall;
		synchronized(capeLock) {
			if(this.cape != null) {
				return; // ignore multiple results
			}
			this.cape = cape;
			toCall = waitingCapeCallbacks;
			waitingCapeCallbacks = null;
		}
		if(toCall != null) {
			List<Consumer<IEaglerPlayerCape>> toCallList = toCall.getList();
			for(int i = 0, l = toCallList.size(); i < l; ++i) {
				try {
					toCallList.get(i).accept(cape);
				}catch(Exception ex) {
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onCapeError() {
		if((int)NODE_ID_HANDLE.getOpaque(this) == -1) {
			connection.onDropPlayer(playerUUID);
		}else {
			KeyedConsumerList<UUID, IEaglerPlayerCape> toCall;
			synchronized(capeLock) {
				if(this.cape != null) {
					return; // ignore multiple results
				}
				toCall = waitingCapeCallbacks;
				waitingCapeCallbacks = null;
			}
			if(toCall != null) {
				List<Consumer<IEaglerPlayerCape>> toCallList = toCall.getList();
				for(int i = 0, l = toCallList.size(); i < l; ++i) {
					try {
						toCallList.get(i).accept(MissingCape.MISSING_CAPE);
					}catch(Exception ex) {
						connection.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void onNodeIDReceived(int nodeId, UUID brandUUID) {
		NODE_ID_HANDLE.setOpaque(this, nodeId);
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
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onNodeIDError() {
		if((int)NODE_ID_HANDLE.getOpaque(this) == -1) {
			connection.onDropPlayer(playerUUID);
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
						connection.logger().error("Caught error from lazy load callback", ex);
					}
				}
			}
		}
	}

	void playerDropped() {
		KeyedConsumerList<UUID, UUID> toCallA;
		KeyedConsumerList<UUID, IEaglerPlayerSkin> toCallB;
		KeyedConsumerList<UUID, IEaglerPlayerCape> toCallC;
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
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
		synchronized(skinLock) {
			toCallB = waitingSkinCallbacks;
			waitingSkinCallbacks = null;
		}
		if(toCallB != null) {
			List<Consumer<IEaglerPlayerSkin>> toCallBList = toCallB.getList();
			for(int i = 0, l = toCallBList.size(); i < l; ++i) {
				try {
					toCallBList.get(i).accept(null);
				}catch(Exception ex) {
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
		synchronized(capeLock) {
			toCallC = waitingCapeCallbacks;
			waitingCapeCallbacks = null;
		}
		if(toCallC != null) {
			List<Consumer<IEaglerPlayerCape>> toCallCList = toCallC.getList();
			for(int i = 0, l = toCallCList.size(); i < l; ++i) {
				try {
					toCallCList.get(i).accept(null);
				}catch(Exception ex) {
					connection.logger().error("Caught error from lazy load callback", ex);
				}
			}
		}
	}

	void onDropPartial(boolean skin, boolean cape) {
		if(skin) {
			this.skin = null;
		}
		if(cape) {
			this.cape = null;
		}
	}

}