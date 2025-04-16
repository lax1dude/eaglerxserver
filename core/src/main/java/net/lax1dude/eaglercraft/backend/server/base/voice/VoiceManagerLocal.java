package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.UUID;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.rpc.EaglerPlayerRPCManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalAllowedEAG;

public class VoiceManagerLocal<PlayerObject> implements IVoiceManagerImpl<PlayerObject> {

	private static final VarHandle SERVER_ENABLE_HANDLE;
	private static final VarHandle LAST_STATE_HANDLE;
	private static final VarHandle ACTIVE_CHANNEL_HANDLE;

	static {
		try {
			MethodHandles.Lookup l = MethodHandles.lookup();
			SERVER_ENABLE_HANDLE = l.findVarHandle(VoiceManagerLocal.class, "isServerEnable", int.class);
			LAST_STATE_HANDLE = l.findVarHandle(VoiceManagerLocal.class, "lastVoiceState", EnumVoiceState.class);
			ACTIVE_CHANNEL_HANDLE = l.findVarHandle(VoiceManagerLocal.class, "activeChannel", VoiceChannel.Context.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	final EaglerPlayerInstance<PlayerObject> player;
	final VoiceServiceLocal<PlayerObject> voice;
	final boolean isBroken;

	final VoiceChannel<PlayerObject>.Context aquireActiveChannel() {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAcquire(this);
	}

	final VoiceChannel<PlayerObject>.Context xchgActiveChannel(VoiceChannel<PlayerObject>.Context newValue) {
		return (VoiceChannel<PlayerObject>.Context) ACTIVE_CHANNEL_HANDLE.getAndSet(this, newValue);
	}

	private boolean isAlive = true;
	private boolean isManaged = true;
	private volatile int isServerEnable = 0;
	private volatile EnumVoiceState lastVoiceState = EnumVoiceState.SERVER_DISABLE;
	private volatile VoiceChannel<PlayerObject>.Context activeChannel = null;
	private IVoiceChannel currentVoiceChannel = DisabledChannel.INSTANCE;

	VoiceManagerLocal(EaglerPlayerInstance<PlayerObject> player, VoiceServiceLocal<PlayerObject> voice) {
		this.player = player;
		this.voice = voice;
		this.isBroken = player.getEaglerProtocol().ver < 5;
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
		return false;
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

	@Override
	public boolean isServerManaged() {
		return isManaged;
	}

	@Override
	public void setServerManaged(boolean managed) {
		isManaged = managed;
	}

	@Override
	public void handleServerPreConnect() {
		if(isManaged) {
			setVoiceChannel0(DisabledChannel.INSTANCE);
		}
	}

	@Override
	public void handleServerPostConnect(String serverName) {
		if(isManaged) {
			setVoiceChannel0(voice.getServerVoiceChannel(serverName));
			onStateChanged();
		}
	}

	@Override
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
		if(newChannel != DisabledChannel.INSTANCE) {
			if(oldChannel == DisabledChannel.INSTANCE) {
				enableVoice();
			}
			((VoiceChannel<PlayerObject>) newChannel).addToChannel(this);
		}else {
			if(oldChannel != DisabledChannel.INSTANCE) {
				((VoiceChannel<PlayerObject>) oldChannel).removeFromChannel(this, true);
			}
			disableVoice();
		}
	}

	private void enableVoice() {
		if((int)SERVER_ENABLE_HANDLE.compareAndExchange(this, 0, 1) == 0) {
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(true, voice.getICEServersStr()));
		}
	}

	private void disableVoice() {
		if((int)SERVER_ENABLE_HANDLE.compareAndExchange(this, 1, 0) != 0) {
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(false, null));
		}
	}

	void onStateChanged() {
		onStateChanged(getVoiceState());
	}

	void onStateChanged(EnumVoiceState state) {
		EnumVoiceState newState = getVoiceState();
		EnumVoiceState oldState = (EnumVoiceState) LAST_STATE_HANDLE.getAndSet(this, newState);
		if(newState != oldState) {
			player.getEaglerXServer().eventDispatcher().dispatchVoiceChangeEvent(player, oldState, newState, null);
			EaglerPlayerRPCManager<PlayerObject> rpcMgr = player.getPlayerRPCManager();
			if(rpcMgr != null) {
				rpcMgr.fireToggleVoice(oldState, newState);
			}
		}
	}

	boolean ratelimitCon() {
		return player.getRateLimits().ratelimitVoiceCon();
	}

	boolean ratelimitReqV5() {
		return isBroken || player.getRateLimits().ratelimitVoiceReq();
	}

	boolean ratelimitICE() {
		return player.getRateLimits().ratelimitVoiceICE();
	}

	@Override
	public void handlePlayerSignalPacketTypeConnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeConnect();
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeRequest(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeICE(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDesc(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnectPeer(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnect() {
		VoiceChannel<PlayerObject>.Context ch = aquireActiveChannel();
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnect();
		}
	}

	@Override
	public void handleBackendMessage(byte[] data) {
		player.logger().warn("Ignoring plugin message from backend on voice RPC channel, server is not in backend-relayed mode");
	}

}
