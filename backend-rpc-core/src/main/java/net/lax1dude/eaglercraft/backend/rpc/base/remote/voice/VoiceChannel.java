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

package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.util.Collectors3;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAnnounce;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCConnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDescription;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCDisconnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCICECandidate;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCPlayerList;

class VoiceChannel<PlayerObject> implements IVoiceChannel {

	public static final long REQUEST_TIMEOUT = 2000l;

	final VoiceServiceRemote<PlayerObject> owner;
	final ConcurrentMap<UUID, Context> connectedPlayers = new ConcurrentHashMap<>();

	VoiceChannel(VoiceServiceRemote<PlayerObject> owner) {
		this.owner = owner;
	}

	void addToChannel(VoiceManagerRemote<PlayerObject> mgr) {
		Context oldContext = mgr.xchgActiveChannel(null);
		boolean connect = false;
		if(oldContext != null) {
			connect = oldContext.finish(false);
		}
		Context newContext = new Context(mgr);
		oldContext = mgr.xchgActiveChannel(newContext);
		if(oldContext != null) {
			connect = oldContext.finish(true);
		}
		if(connect) {
			newContext.handleVoiceSignalPacketTypeConnect();
		}
	}

	void removeFromChannel(VoiceManagerRemote<PlayerObject> mgr, boolean dead) {
		Context oldContext = mgr.xchgActiveChannel(null);
		if(oldContext != null) {
			oldContext.finish(dead);
		}
	}

	interface IVoiceState {
		int expired(long nanos);
	}

	static class PendingState implements IVoiceState {

		final long expiry;

		PendingState(long createdAt) {
			expiry = createdAt + (REQUEST_TIMEOUT * 1000000l);
		}

		@Override
		public int expired(long nanos) {
			return nanos > expiry ? 1 : 0;
		}

	}

	static final IVoiceState ESTABLISHED = (l) -> -1;

	class Context extends HashMap<Context, IVoiceState> {

		final VoiceManagerRemote<PlayerObject> mgr;
		final UUID selfUUID;
		long lastFlush = 0;
		boolean expirable = false;

		private Context(VoiceManagerRemote<PlayerObject> mgr) {
			this.mgr = mgr;
			this.selfUUID = mgr.player.getUniqueId();
		}

		void handleVoiceSignalPacketTypeConnect() {
			boolean empty = connectedPlayers.isEmpty();
			if(connectedPlayers.putIfAbsent(selfUUID, this) != null) {
				return;
			}
			mgr.onStateChanged(EnumVoiceState.ENABLED);
			if(empty) {
				return;
			}
			Object[] allPlayers = connectedPlayers.values().toArray();
			int len = allPlayers.length;
			EaglerVCPacket announcePacket = new SPacketVCAnnounce(selfUUID.getMostSignificantBits(),
					selfUUID.getLeastSignificantBits());
			SPacketVCPlayerList.UserData[] userDatas = new SPacketVCPlayerList.UserData[len];
			for(int i = 0; i < len; ++i) {
				Context ctx = (Context) allPlayers[i];
				VoiceManagerRemote<PlayerObject> ctxMgr = ctx.mgr;
				userDatas[i] = new SPacketVCPlayerList.UserData(ctx.selfUUID.getMostSignificantBits(),
						ctx.selfUUID.getLeastSignificantBits(), ctxMgr.player.getUsername());
				if(ctx != this) {
					ctxMgr.writeOutboundVoicePacket(announcePacket);
				}
			}
			EaglerVCPacket packetToBroadcast = new SPacketVCPlayerList(Arrays.asList(userDatas));
			for(int i = 0; i < len; ++i) {
				((Context) allPlayers[i]).mgr.writeOutboundVoicePacket(packetToBroadcast);
			}
		}

		private boolean putRequest(Context other) {
			long nanos = System.nanoTime();
			if(putIfAbsent(other, new PendingState(nanos)) == null) {
				if(!expirable) {
					lastFlush = nanos;
					expirable = true;
				}
				return true;
			}else {
				return false;
			}
		}

		private IVoiceState checkState(Context other) {
			long nanos = System.nanoTime();
			if(expirable) {
				if(nanos - lastFlush > (30l * 1000l * 1000l * 1000l)) {
					lastFlush = nanos;
					expirable = false;
					Iterator<IVoiceState> itr = values().iterator();
					int i;
					while(itr.hasNext()) {
						IVoiceState state = itr.next();
						i = state.expired(nanos);
						if(i == 1) {
							itr.remove();
						}else if(i == 0) {
							expirable = true;
						}
					}
					return get(other);
				}
			}
			IVoiceState state = get(other);
			if(state != null && state.expired(nanos) == 1) {
				remove(other);
				return null;
			}
			return state;
		}

		void handleVoiceSignalPacketTypeRequest(UUID player) {
			Context other = connectedPlayers.get(player);
			if(other != null && other != this) {
				IVoiceState newState = null;
				synchronized(other) {
					IVoiceState otherState = other.checkState(this);
					if(otherState == ESTABLISHED) {
						return;
					}else if(otherState != null) {
						newState = ESTABLISHED;
						other.put(this, newState);
					}
				}
				synchronized(this) {
					if(newState == null) {
						putRequest(other);
						return;
					}else {
						put(other, newState);
					}
				}
				other.mgr.writeOutboundVoicePacket(new SPacketVCConnectPeer(selfUUID.getMostSignificantBits(),
						selfUUID.getLeastSignificantBits(), false));
				mgr.writeOutboundVoicePacket(new SPacketVCConnectPeer(player.getMostSignificantBits(),
						player.getLeastSignificantBits(), true));
			}
		}

		void handleVoiceSignalPacketTypeICE(UUID player, byte[] str) {
			Context other = connectedPlayers.get(player);
			if(other != null && other != this) {
				synchronized(this) {
					if(checkState(other) != ESTABLISHED) {
						return;
					}
				}
				other.mgr.writeOutboundVoicePacket(new SPacketVCICECandidate(selfUUID.getMostSignificantBits(),
						selfUUID.getLeastSignificantBits(), str));
			}
		}

		void handleVoiceSignalPacketTypeDesc(UUID player, byte[] str) {
			Context other = connectedPlayers.get(player);
			if(other != null && other != this) {
				synchronized(this) {
					if(checkState(other) != ESTABLISHED) {
						return;
					}
				}
				other.mgr.writeOutboundVoicePacket(new SPacketVCDescription(selfUUID.getMostSignificantBits(),
						selfUUID.getLeastSignificantBits(), str));
			}
		}

		void handleVoiceSignalPacketTypeDisconnectPeer(UUID player) {
			Context other = connectedPlayers.get(player);
			if(other != null && other != this) {
				IVoiceState state;
				synchronized(this) {
					state = remove(other);
				}
				if(state == ESTABLISHED) {
					synchronized(other) {
						other.remove(this);
					}
					other.mgr.writeOutboundVoicePacket(new SPacketVCDisconnectPeer(
							selfUUID.getMostSignificantBits(), selfUUID.getLeastSignificantBits()));
					mgr.writeOutboundVoicePacket(new SPacketVCDisconnectPeer(
							player.getMostSignificantBits(), player.getLeastSignificantBits()));
				}
			}
		}

		void handleVoiceSignalPacketTypeDisconnect() {
			handleRemove(true);
		}

		boolean finish(boolean dead) {
			return handleRemove(dead);
		}

		private boolean handleRemove(boolean dead) {
			if(connectedPlayers.remove(selfUUID) == null) {
				return false;
			}
			mgr.onStateChanged(EnumVoiceState.DISABLED);
			Object[] allPlayers = connectedPlayers.values().toArray();
			int len = allPlayers.length;
			Object[] toNotify;
			synchronized(this) {
				toNotify = keySet().toArray();
				clear();
				expirable = false;
			}
			int cnt = toNotify.length;
			if(cnt > 0) {
				EaglerVCPacket pkt = new SPacketVCDisconnectPeer(selfUUID.getMostSignificantBits(),
						selfUUID.getLeastSignificantBits());
				for(int i = 0; i < cnt; ++i) {
					Context ctx = (Context) toNotify[i];
					IVoiceState voice;
					synchronized(ctx) {
						voice = ctx.remove(this);
					}
					if(voice != null) {
						if(!dead) {
							UUID uuid = ctx.selfUUID;
							mgr.writeOutboundVoicePacket(new SPacketVCDisconnectPeer(uuid.getMostSignificantBits(),
									uuid.getLeastSignificantBits()));
						}
						if(voice == ESTABLISHED) {
							ctx.mgr.writeOutboundVoicePacket(pkt);
						}
					}
				}
			}
			if(len > 0) {
				SPacketVCPlayerList.UserData[] userDatas = new SPacketVCPlayerList.UserData[len];
				for(int i = 0; i < len; ++i) {
					Context ctx = (Context) allPlayers[i];
					PlayerInstanceRemote<PlayerObject> ctxPlayer = ctx.mgr.player;
					UUID ctxUUID = ctxPlayer.getUniqueId();
					userDatas[i] = new SPacketVCPlayerList.UserData(ctxUUID.getMostSignificantBits(),
							ctxUUID.getLeastSignificantBits(), ctxPlayer.getUsername());
				}
				EaglerVCPacket packetToBroadcast = new SPacketVCPlayerList(Arrays.asList(userDatas));
				for(int i = 0; i < len; ++i) {
					((Context) allPlayers[i]).mgr.writeOutboundVoicePacket(packetToBroadcast);
				}
			}
			return true;
		}

		boolean isConnected() {
			return connectedPlayers.containsKey(selfUUID);
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}

		@Override
		public boolean equals(Object o) {
			return this == o;
		}

	}

	Collection<IEaglerPlayer<PlayerObject>> listConnectedPlayers() {
		return connectedPlayers.values().stream().map((ctx) -> ctx.mgr.player)
				.collect(Collectors3.toImmutableList());
	}

	@Override
	public boolean isManaged() {
		return false;
	}

	@Override
	public boolean isDisabled() {
		return false;
	}

}
