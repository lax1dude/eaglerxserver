package net.lax1dude.eaglercraft.backend.rpc.base.remote.voice;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.rpc.adapter.IPlatformLogger;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.EaglerXBackendRPCRemote;
import net.lax1dude.eaglercraft.backend.rpc.base.remote.PlayerInstanceRemote;
import net.lax1dude.eaglercraft.backend.voice.protocol.EaglerVCProtocol;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.EaglerVCPacket;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.client.CPacketVCCapable;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCAllowed;
import net.lax1dude.eaglercraft.backend.voice.protocol.pkt.server.SPacketVCCapable;

public class VoiceManagerRemote<PlayerObject> extends SerializationContext implements IVoiceManager<PlayerObject> {

	private static final VarHandle SERVER_ENABLE_HANDLE;
	private static final VarHandle LAST_STATE_HANDLE;
	private static final VarHandle ACTIVE_CHANNEL_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			SERVER_ENABLE_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "isServerEnable", int.class);
			LAST_STATE_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "lastVoiceState", EnumVoiceState.class);
			ACTIVE_CHANNEL_HANDLE = l.findVarHandle(VoiceManagerRemote.class, "activeChannel", VoiceChannel.Context.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	final VoiceChannel<PlayerObject>.Context aquireActiveChannel() {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAcquire(this);
	}

	final VoiceChannel<PlayerObject>.Context xchgActiveChannel(VoiceChannel<PlayerObject>.Context newValue) {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAndSet(this, newValue);
	}

	final PlayerInstanceRemote<PlayerObject> player;
	final VoiceServiceRemote<PlayerObject> voice;

	private boolean isAlive = true;
	private volatile int isServerEnable = 0;
	private volatile EnumVoiceState lastVoiceState = EnumVoiceState.SERVER_DISABLE;
	private volatile VoiceChannel<PlayerObject>.Context activeChannel = null;
	private IVoiceChannel currentVoiceChannel = DisabledChannel.INSTANCE;
	private BackendVCProtocolHandler handler = null;

	VoiceManagerRemote(PlayerInstanceRemote<PlayerObject> player, VoiceServiceRemote<PlayerObject> voice) {
		super(player.serializationContext);
		this.player = player;
		this.voice = voice;
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
	protected IPlatformLogger logger() {
		return player.logger();
	}

	public boolean isVoiceCapable() {
		return (int)SERVER_ENABLE_HANDLE.getAcquire(this) != 0;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		if(currentVoiceChannel != DisabledChannel.INSTANCE) {
			VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
			if(ch != null) {
				return ch.isConnected() ? EnumVoiceState.ENABLED : EnumVoiceState.DISABLED;
			}
		}
		return EnumVoiceState.SERVER_DISABLE;
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return currentVoiceChannel;
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		setVoiceChannel0(channel);
		onStateChanged();
	}

	private void setVoiceChannel0(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
		}
		if (channel != DisabledChannel.INSTANCE && (!(channel instanceof VoiceChannel ch) || ch.owner != voice)) {
			throw new IllegalArgumentException("Unknown voice channel");
		}
		IVoiceChannel oldChannel;
		synchronized(this) {
			if(!isAlive) {
				return;
			}
			oldChannel = currentVoiceChannel;
			if(channel == oldChannel) {
				return;
			}
			currentVoiceChannel = channel;
		}
		switchChannels(oldChannel, channel);
	}

	public void destroyVoiceManager() {
		IVoiceChannel oldChannel;
		synchronized(this) {
			if(!isAlive) {
				return;
			}
			isAlive = false;
			oldChannel = currentVoiceChannel;
			if(DisabledChannel.INSTANCE == oldChannel) {
				return;
			}
			currentVoiceChannel = DisabledChannel.INSTANCE;
		}
		((VoiceChannel<PlayerObject>) oldChannel).removeFromChannel(this, true);
	}

	private void switchChannels(IVoiceChannel oldChannel, IVoiceChannel newChannel) {
		if(oldChannel != DisabledChannel.INSTANCE) {
			((VoiceChannel<PlayerObject>) oldChannel).removeFromChannel(this, newChannel == DisabledChannel.INSTANCE);
		}else {
			enableVoice();
		}
		if(newChannel != DisabledChannel.INSTANCE) {
			((VoiceChannel<PlayerObject>) newChannel).addToChannel(this);
		}else {
			disableVoice();
		}
	}

	public void handleInboundVoiceMessage(byte[] packet) {
		if((int)SERVER_ENABLE_HANDLE.getAcquire(this) != 0) {
			EaglerVCPacket pkt;
			try {
				pkt = deserialize(EaglerVCProtocol.V1, packet);
			}catch(Exception ex) {
				onException(ex);
				return;
			}
			try {
				pkt.handlePacket(handler);
			} catch (Exception ex) {
				onException(new IllegalStateException(
						"Failed to handle inbound voice RPC packet: " + pkt.getClass().getSimpleName(), ex));
			}
		}else {
			EaglerVCPacket pkt;
			try {
				pkt = deserialize(EaglerVCProtocol.INIT, packet);
				if(!(pkt instanceof CPacketVCCapable)) {
					throw new IOException("Unexpected packet type: " + pkt.getClass().getSimpleName());
				}
			}catch(Exception ex) {
				onException(ex);
				return;
			}
			CPacketVCCapable pktt = (CPacketVCCapable) pkt;
			eagler: {
				int[] vers = pktt.versions;
				for(int i = 0; i < vers.length; ++i) {
					if(vers[i] == 1) {
						break eagler;
					}
				}
				logger().error("Unsupported voice RPC handshake recieved!");
				return;
			}
			EaglerXBackendRPCRemote<PlayerObject> server = player.getEaglerXBackendRPC();
			VoiceServiceRemote<PlayerObject> service = (VoiceServiceRemote<PlayerObject>) server.getVoiceService();
			try {
				packet = serialize(EaglerVCProtocol.INIT,
						new SPacketVCCapable(1, false, service.getOverrideICEServers(), service.getICEServersStr()));
			}catch(Exception ex) {
				onException(ex);
				return;
			}
			handler = new BackendV1VCProtocolHandler(this);
			SERVER_ENABLE_HANDLE.setRelease(this, 1);
			player.getPlatformPlayer().sendData(server.getChannelVoiceName(), packet);
			player.getEaglerXBackendRPC().getPlatform().eventDispatcher().dispatchVoiceCapableEvent(player);
		}
	}

	private void onException(Exception ex) {
		logger().error("Caught exception while processing voice RPC packets!", ex);
	}

	void writeOutboundVoicePacket(EaglerVCPacket packet) {
		byte[] pkt;
		try {
			pkt = serialize(EaglerVCProtocol.V1, packet);
		}catch(Exception ex) {
			onException(ex);
			return;
		}
		player.getPlatformPlayer().sendData(player.getEaglerXBackendRPC().getChannelVoiceName(), pkt);
	}

	private void enableVoice() {
		if((int)SERVER_ENABLE_HANDLE.compareAndExchange(this, 1, 2) == 1) {
			writeOutboundVoicePacket(new SPacketVCAllowed(true));
		}
	}

	private void disableVoice() {
		if((int)SERVER_ENABLE_HANDLE.compareAndExchange(this, 2, 1) == 2) {
			writeOutboundVoicePacket(new SPacketVCAllowed(false));
		}
	}

	void onStateChanged() {
		onStateChanged(getVoiceState());
	}

	void onStateChanged(EnumVoiceState newState) {
		EnumVoiceState oldState = (EnumVoiceState) LAST_STATE_HANDLE.getAndSet(this, newState);
		if(newState != oldState) {
			player.getEaglerXBackendRPC().getPlatform().eventDispatcher().dispatchVoiceChangeEvent(player, oldState, newState);
		}
	}

	void handlePlayerSignalPacketTypeConnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeConnect();
		}
	}

	void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeRequest(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeICE(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDesc(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnectPeer(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	void handlePlayerSignalPacketTypeDisconnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnect();
		}
	}

}
