package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceX;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.server.base.rpc.EaglerPlayerRPCManager;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalAllowedEAG;

public class VoiceManagerLocal<PlayerObject> implements IVoiceManagerImpl<PlayerObject> {

	final EaglerPlayerInstance<PlayerObject> player;
	final VoiceServiceLocal<PlayerObject> voice;
	final boolean isBroken;

	volatile VoiceChannel<PlayerObject>.Context activeChannel = null;

	private boolean isAlive = true;
	private boolean isManaged = true;
	private final AtomicBoolean isServerEnable = new AtomicBoolean(false);
	private final AtomicReference<EnumVoiceState> lastVoiceState = new AtomicReference<>(EnumVoiceState.SERVER_DISABLE);
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
	public IVoiceServiceX<PlayerObject> getVoiceService() {
		return voice;
	}

	@Override
	public boolean isBackendRelayMode() {
		return false;
	}

	@Override
	public EnumVoiceState getVoiceState() {
		if(currentVoiceChannel != DisabledChannel.INSTANCE) {
			VoiceChannel<PlayerObject>.Context ch = activeChannel;
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
		if (channel != DisabledChannel.INSTANCE
				&& (!(channel instanceof VoiceChannel) || ((VoiceChannel<?>) channel).owner != voice)) {
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
	public void handleServerChanged(String serverName) {
		if(isManaged) {
			setVoiceChannel0(DisabledChannel.INSTANCE);
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

	private void enableVoice() {
		if(!isServerEnable.compareAndExchange(false, true)) {
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(true, voice.getICEServersStr()));
		}
	}

	private void disableVoice() {
		if(isServerEnable.compareAndExchange(true, false)) {
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(false, null));
		}
	}

	private void onStateChanged() {
		EnumVoiceState newState = getVoiceState();
		EnumVoiceState oldState = lastVoiceState.getAndSet(newState);
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
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeConnect();
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeRequest(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeRequest(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeICE(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeICE(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDesc(long playerUUIDMost, long playerUUIDLeast, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDesc(new UUID(playerUUIDMost, playerUUIDLeast), str);
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnectPeer(long playerUUIDMost, long playerUUIDLeast) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnectPeer(new UUID(playerUUIDMost, playerUUIDLeast));
		}
	}

	@Override
	public void handlePlayerSignalPacketTypeDisconnect() {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnect();
		}
	}

	@Override
	public void handleBackendMessage(byte[] data) {
		player.logger().warn("Ignoring plugin message from backend on voice RPC channel, server is not in backend-relayed mode");
	}

}
