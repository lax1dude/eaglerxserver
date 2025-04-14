package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.lax1dude.eaglercraft.backend.server.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.WrongVCPacketException;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCCapable;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCConnect;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCConnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCDescription;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCDisconnect;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCDisconnectPeer;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCICECandidate;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCCapable;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCPlayerList.UserData;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalAllowedEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalConnectAnnounceV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalConnectV4EAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalDescEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalDisconnectPeerEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalGlobalEAG;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalICEEAG;

public class VoiceManagerRemote<PlayerObject> extends SerializationContext implements IVoiceManagerImpl<PlayerObject> {

	private static final VarHandle STATE_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			STATE_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "state", int.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	final EaglerPlayerInstance<PlayerObject> player;
	final VoiceServiceRemote<PlayerObject> voice;
	private volatile int state = -1;
	private ServerVCProtocolHandler handler;
	final boolean isBroken;

	VoiceManagerRemote(EaglerPlayerInstance<PlayerObject> player, VoiceServiceRemote<PlayerObject> voice) {
		super(player.getSerializationContext());
		this.player = player;
		this.voice = voice;
		this.isBroken = player.getEaglerProtocol().ver < 5;
	}

	@Override
	protected IPlatformLogger logger() {
		return player.logger();
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceService<PlayerObject> getVoiceService() {
		return voice;
	}

	@Override
	public boolean isBackendRelayMode() {
		return true;
	}

	private int stateXchg(int newValue) {
		return (int)STATE_HANDLE.getAndSet(this, newValue);
	}

	private int stateCmpXchg(int oldValue, int newValue) {
		return (int)STATE_HANDLE.compareAndExchange(this, oldValue, newValue);
	}

	@Override
	public EnumVoiceState getVoiceState() {
		return switch((int)STATE_HANDLE.getAcquire(this)) {
		default -> EnumVoiceState.SERVER_DISABLE;
		case 1 -> EnumVoiceState.DISABLED;
		case 2 -> EnumVoiceState.ENABLED;
		};
	}

	private boolean isVoiceEnabled() {
		return (int)STATE_HANDLE.getAcquire(this) == 2;
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		throw VoiceServiceRemote.backendRelayMode();
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		throw VoiceServiceRemote.backendRelayMode();
	}

	@Override
	public boolean isServerManaged() {
		throw VoiceServiceRemote.backendRelayMode();
	}

	@Override
	public void setServerManaged(boolean managed) {
		throw VoiceServiceRemote.backendRelayMode();
	}

	@Override
	public void handleBackendMessage(byte[] data) {
		EaglerVCPacket pkt;
		eagler: if((int)STATE_HANDLE.getAcquire(this) == -1) {
			synchronized(this) {
				if((int)STATE_HANDLE.getAcquire(this) != -1) {
					break eagler;
				}
				try {
					pkt = deserialize(EaglerVCProtocol.INIT, data);
				} catch (Exception e) {
					handleException(e);
					return;
				}
				handleBackendHandshake(pkt);
				return;
			}
		}
		try {
			pkt = deserialize(EaglerVCProtocol.V1, data);
		} catch (Exception e) {
			handleException(e);
			return;
		}
		try {
			pkt.handlePacket(handler);
		} catch (Exception e) {
			handleException(new IllegalStateException(
					"Failed to handle inbound voice RPC packet: " + pkt.getClass().getSimpleName(), e));
		}
	}

	private void handleBackendHandshake(EaglerVCPacket packet) {
		if(packet instanceof SPacketVCCapable pkt) {
			if(pkt.version != 1) {
				throw new IllegalStateException("Wrong protocol version selected: " + pkt.version);
			}
			handler = new ServerV1VCProtocolHandler(this, internStrings(pkt.iceServers), pkt.overrideICE);
			if(pkt.allowed) {
				STATE_HANDLE.setRelease(this, 1);
				voiceEnabled();
			}else {
				STATE_HANDLE.setRelease(this, 0);
			}
		}else {
			throw new WrongVCPacketException();
		}
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private static String[] internStrings(String[] strs) {
		if(strs.length == 0) {
			return EMPTY_STRING_ARRAY;
		}
		for(int i = 0; i < strs.length; ++i) {
			strs[i] = strs[i].intern();
		}
		return strs;
	}

	private void handleException(Exception e) {
		player.logger().error("Caught exception handling voice RPC packet from backend", e);
	}

	private void sendBackendMessage(EaglerVCPacket packet) {
		sendBackendMessage(EaglerVCProtocol.V1, packet);
	}

	private void sendBackendMessage(EaglerVCProtocol protocol, EaglerVCPacket packet) {
		byte[] pkt;
		try {
			pkt = serialize(protocol, packet);
		} catch (IOException e) {
			handleException(e);
			return;
		}
		player.getPlatformPlayer().sendDataBackend(voice.getRPCChannel(), pkt);
	}

	@Override
	public void handleServerChanged(String serverName) {
		int lastState = stateXchg(-1);
		handler = null;
		if(lastState != 0 && lastState != -1) {
			voiceDisabled(lastState == 2);
		}
		sendBackendMessage(EaglerVCProtocol.INIT, new CPacketVCCapable(new int[] { 1 }));
	}

	private String[] concatICEServers() {
		ServerVCProtocolHandler h = handler;
		if(h != null) {
			String[] iceServers = h.iceServerStash;
			if(h.iceServerOverride) {
				return iceServers;
			}else {
				Set<String> joined = new HashSet<>();
				addAll(joined, voice.iceServersStr());
				addAll(joined, iceServers);
				return joined.toArray(new String[joined.size()]);
			}
		}else {
			return voice.iceServersStr();
		}
	}

	private static void addAll(Set<String> set, String[] strs) {
		for(int i = 0; i < strs.length; ++i) {
			set.add(strs[i]);
		}
	}

	private void voiceEnabled() {
		player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(true, concatICEServers()));
		player.getEaglerXServer().eventDispatcher().dispatchVoiceChangeEvent(player, EnumVoiceState.SERVER_DISABLE,
				EnumVoiceState.DISABLED, null);
	}

	private void voiceConnected() {
		player.getEaglerXServer().eventDispatcher().dispatchVoiceChangeEvent(player, EnumVoiceState.DISABLED,
				EnumVoiceState.ENABLED, null);
	}

	private void voiceDisconnected() {
		player.getEaglerXServer().eventDispatcher().dispatchVoiceChangeEvent(player, EnumVoiceState.ENABLED,
				EnumVoiceState.DISABLED, null);
	}

	private void voiceDisabled(boolean wasConnected) {
		player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(false, null));
		player.getEaglerXServer().eventDispatcher().dispatchVoiceChangeEvent(player,
				wasConnected ? EnumVoiceState.ENABLED : EnumVoiceState.DISABLED, EnumVoiceState.SERVER_DISABLE, null);
	}

	@Override
	public void destroyVoiceManager() {
	}

	private boolean ratelimitCon() {
		return player.getRateLimits().ratelimitVoiceCon();
	}

	private boolean ratelimitReqV5() {
		return isBroken || player.getRateLimits().ratelimitVoiceReq();
	}

	private boolean ratelimitICE() {
		return player.getRateLimits().ratelimitVoiceICE();
	}

	@Override
	public void handlePlayerSignalPacketTypeConnect() {
		if(ratelimitCon() && stateCmpXchg(1, 2) == 1) {
			sendBackendMessage(new CPacketVCConnect());
			voiceConnected();
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast) {
		if(isVoiceEnabled() && ratelimitReqV5()) {
			sendBackendMessage(new CPacketVCConnectPeer(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		if(isVoiceEnabled() && ratelimitICE()) {
			sendBackendMessage(new CPacketVCICECandidate(playerUUIDMost, playerUUIDLeast, str));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		if(isVoiceEnabled() && ratelimitICE()) {
			sendBackendMessage(new CPacketVCDescription(playerUUIDMost, playerUUIDLeast, str));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast) {
		if(isVoiceEnabled()) {
			sendBackendMessage(new CPacketVCDisconnectPeer(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnect() {
		if(stateCmpXchg(2, 1) == 2) {
			sendBackendMessage(new CPacketVCDisconnect());
			voiceDisconnected();
		}
	}

	public void handleBackendSignalPacketAllowed(boolean allowed) {
		if(allowed) {
			if(stateCmpXchg(0, 1) == 0) {
				voiceEnabled();
			}
		}else {
			int lastState = stateXchg(0);
			if(lastState != 0) {
				if(lastState == -1) {
					STATE_HANDLE.setOpaque(this, -1);
					throw new IllegalStateException("shit");
				}
				voiceDisabled(lastState == 2);
			}
		}
	}

	public void handleBackendSignalPacketPlayerList(Collection<UserData> users) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalGlobalEAG(users.stream().map(
					(data) -> new SPacketVoiceSignalGlobalEAG.UserData(data.uuidMost, data.uuidLeast, data.username))
					.collect(Collectors.toList())));
		}
	}

	public void handleBackendSignalPacketAnnounce(long uuidMost, long uuidLeast) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalConnectAnnounceV4EAG(uuidMost, uuidLeast));
		}
	}

	public void handleBackendSignalPacketConnectPeer(long uuidMost, long uuidLeast, boolean offer) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalConnectV4EAG(uuidMost, uuidLeast, offer));
		}
	}

	public void handleBackendSignalPacketDisconnectPeer(long uuidMost, long uuidLeast) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalDisconnectPeerEAG(uuidMost, uuidLeast));
		}
	}

	public void handleBackendSignalPacketDescription(long uuidMost, long uuidLeast, byte[] desc) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalDescEAG(uuidMost, uuidLeast, desc));
		}
	}

	public void handleBackendSignalPacketICECandidate(long uuidMost, long uuidLeast, byte[] ice) {
		if(isVoiceEnabled()) {
			player.sendEaglerMessage(new SPacketVoiceSignalICEEAG(uuidMost, uuidLeast, ice));
		}
	}

}
