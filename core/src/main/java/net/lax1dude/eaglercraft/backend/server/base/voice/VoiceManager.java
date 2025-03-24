package net.lax1dude.eaglercraft.backend.server.base.voice;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceImpl;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalAllowedEAG;

public class VoiceManager<PlayerObject> implements IVoiceManagerImpl<PlayerObject> {

	final EaglerPlayerInstance<PlayerObject> player;
	final VoiceService<PlayerObject> voice;

	volatile VoiceChannel<PlayerObject>.Context activeChannel = null;

	private boolean isAlive = true;
	private boolean isManaged = true;
	private final AtomicBoolean isServerEnable = new AtomicBoolean(false);
	private final AtomicReference<EnumVoiceState> lastVoiceState = new AtomicReference<>(EnumVoiceState.SERVER_DISABLE);
	private IVoiceChannel currentVoiceChannel = DisabledChannel.INSTANCE;

	VoiceManager(EaglerPlayerInstance<PlayerObject> player, VoiceService<PlayerObject> voice) {
		this.player = player;
		this.voice = voice;
	}

	@Override
	public IEaglerPlayer<PlayerObject> getPlayer() {
		return player;
	}

	@Override
	public IVoiceServiceImpl<PlayerObject> getVoiceService() {
		return voice;
	}

	@Override
	public boolean isBackendRelayMode() {
		return voice.isBackendRelayMode();
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

	public void handleServerChanged(String serverName) {
		if(isManaged) {
			setVoiceChannel0(DisabledChannel.INSTANCE);
			setVoiceChannel0(voice.getServerVoiceChannel(serverName));
			onStateChanged();
		}
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
		}
	}

	public void handleVoiceSignalPacketTypeConnect() {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeConnect();
		}
	}

	public void handleVoiceSignalPacketTypeRequest(UUID player) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeRequest(player);
		}
	}

	public void handleVoiceSignalPacketTypeICE(UUID player, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeICE(player, str);
		}
	}

	public void handleVoiceSignalPacketTypeDesc(UUID player, byte[] str) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDesc(player, str);
		}
	}

	public void handleVoiceSignalPacketTypeDisconnectPeer(UUID player) {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnectPeer(player);
		}
	}

	public void handleVoiceSignalPacketTypeDisconnect() {
		VoiceChannel<PlayerObject>.Context ch = activeChannel;
		if(ch != null) {
			ch.handleVoiceSignalPacketTypeDisconnect();
		}
	}

}
