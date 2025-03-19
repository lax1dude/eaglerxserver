package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalConnectAnnounceV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalConnectV3EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalDescEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalDisconnectPeerEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalGlobalEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalICEEAG;

class VoiceChannel<PlayerObject> implements IVoiceChannel {

	public static final long REQUEST_TIMEOUT = 2000l;

	final VoiceService<PlayerObject> owner;
	final ConcurrentMap<UUID, Context> connectedPlayers = new ConcurrentHashMap<>();

	VoiceChannel(VoiceService<PlayerObject> owner) {
		this.owner = owner;
	}

	void addToChannel(VoiceManager<PlayerObject> mgr) {
		Context oldContext = mgr.activeChannel;
		if(oldContext != null) {
			mgr.activeChannel = null;
			oldContext.finish();
		}
		Context newContext = new Context(mgr);
		newContext.init();
		mgr.activeChannel = newContext;
	}

	void removeFromChannel(VoiceManager<PlayerObject> mgr) {
		Context oldContext = mgr.activeChannel;
		if(oldContext != null) {
			mgr.activeChannel = null;
			oldContext.finish();
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

		final VoiceManager<PlayerObject> mgr;
		final UUID selfUUID;
		long lastFlush = 0;
		boolean expirable = false;

		private Context(VoiceManager<PlayerObject> mgr) {
			this.mgr = mgr;
			this.selfUUID = mgr.player.getUniqueId();
		}

		void init() {
		}

		void handleVoiceSignalPacketTypeConnect() {
			boolean empty = connectedPlayers.isEmpty();
			if(connectedPlayers.putIfAbsent(selfUUID, this) != null) {
				return;
			}
			if(empty) {
				return;
			}
			Object[] allPlayers = connectedPlayers.values().toArray();
			int len = allPlayers.length;
			GameMessagePacket v3p = null;
			GameMessagePacket v4p = null;
			SPacketVoiceSignalGlobalEAG.UserData[] userDatas = new SPacketVoiceSignalGlobalEAG.UserData[len];
			for(int i = 0; i < len; ++i) {
				Context ctx = (Context) allPlayers[i];
				EaglerPlayerInstance<PlayerObject> ctxPlayer = ctx.mgr.player;
				UUID ctxUUID = ctxPlayer.getUniqueId();
				userDatas[i] = new SPacketVoiceSignalGlobalEAG.UserData(ctxUUID.getMostSignificantBits(),
						ctxUUID.getLeastSignificantBits(), ctxPlayer.getUsername());
				if (ctxPlayer.getEaglerProtocol().ver <= 3) {
					ctxPlayer.sendEaglerMessage(v3p == null
							? (v3p = new SPacketVoiceSignalConnectV3EAG(selfUUID.getMostSignificantBits(),
									selfUUID.getLeastSignificantBits(), true, false))
							: v3p);
				} else {
					ctxPlayer.sendEaglerMessage(v4p == null
							? (v4p = new SPacketVoiceSignalConnectAnnounceV4EAG(
									selfUUID.getMostSignificantBits(), selfUUID.getLeastSignificantBits()))
							: v4p);
				}
			}
			GameMessagePacket packetToBroadcast = new SPacketVoiceSignalGlobalEAG(Arrays.asList(userDatas));
			for(int i = 0; i < len; ++i) {
				((Context) allPlayers[i]).mgr.player.sendEaglerMessage(packetToBroadcast);
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
			}
			return state;
		}

		void handleVoiceSignalPacketTypeRequest(UUID player) {
			Context other = connectedPlayers.get(player);
			if(other != null && other != this) {
				//TODO
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
				other.mgr.player.sendEaglerMessage(new SPacketVoiceSignalICEEAG(selfUUID.getMostSignificantBits(),
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
				other.mgr.player.sendEaglerMessage(new SPacketVoiceSignalDescEAG(selfUUID.getMostSignificantBits(),
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
					other.mgr.player.sendEaglerMessage(new SPacketVoiceSignalDisconnectPeerEAG(
							selfUUID.getMostSignificantBits(), selfUUID.getLeastSignificantBits()));
					mgr.player.sendEaglerMessage(new SPacketVoiceSignalDisconnectPeerEAG(
							player.getMostSignificantBits(), player.getLeastSignificantBits()));
				}
			}
		}

		void handleVoiceSignalPacketTypeDisconnect() {
			//TODO
		}

		void finish() {
			//TODO
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

	void handleVoiceSignalPacketTypeDisconnect(VoiceManager<PlayerObject> mgr) {
		if(connectedPlayers.remove(mgr.player.getUniqueId()) == null) {
			return;
		}
		
	}

	Collection<IEaglerPlayer<PlayerObject>> listConnectedPlayers() {
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
