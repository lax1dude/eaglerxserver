package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.voice.api.IVoicePlayer;
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
		if(oldContext != null) {
			oldContext.finish(false);
		}
		oldContext = mgr.xchgActiveChannel(new Context(mgr));
		if(oldContext != null) {
			oldContext.finish(true);
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
			if(!mgr.ratelimitCon()) {
				return;
			}
			boolean empty = connectedPlayers.isEmpty();
			if(connectedPlayers.putIfAbsent(selfUUID, this) != null) {
				return;
			}
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
				UUID ctxUUID = ctxMgr.player.getUniqueId();
				userDatas[i] = new SPacketVCPlayerList.UserData(ctxUUID.getMostSignificantBits(),
						ctxUUID.getLeastSignificantBits(), ctxMgr.player.getUsername());
				ctxMgr.writeOutboundVoicePacket(announcePacket);
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
			if(!mgr.ratelimitReqV5()) {
				return;
			}
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
			if(!mgr.ratelimitICE()) {
				return;
			}
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
			if(!mgr.ratelimitICE()) {
				return;
			}
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

		void finish(boolean dead) {
			handleRemove(dead);
		}

		private void handleRemove(boolean dead) {
			if(connectedPlayers.remove(selfUUID) == null) {
				return;
			}
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

	Collection<IVoicePlayer<PlayerObject>> listConnectedPlayers() {
		return connectedPlayers.values().stream().map((ctx) -> ctx.mgr.player)
				.collect(ImmutableList.toImmutableList());
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
