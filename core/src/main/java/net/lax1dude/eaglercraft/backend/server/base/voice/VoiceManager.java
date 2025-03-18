package net.lax1dude.eaglercraft.backend.server.base.voice;

import io.netty.channel.EventLoop;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;
import net.lax1dude.eaglercraft.backend.server.base.EaglerPlayerInstance;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.server.SPacketVoiceSignalAllowedEAG;

public class VoiceManager<PlayerObject> implements IVoiceManager<PlayerObject> {

	private final EaglerPlayerInstance<PlayerObject> player;
	private final VoiceService<PlayerObject> voice;
	private boolean isAlive = true;
	private IVoiceChannel currentVoiceChannel = DisabledChannel.INSTANCE;

	private boolean isServerEnable = false;

	VoiceManager(EaglerPlayerInstance<PlayerObject> player, VoiceService<PlayerObject> voice) {
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
	public boolean isBackendRelayMode() {
		return voice.isBackendRelayMode();
	}

	@Override
	public EnumVoiceState getVoiceState() {
		if(currentVoiceChannel == DisabledChannel.INSTANCE) {
			return EnumVoiceState.SERVER_DISABLE;
		}else {
			return null; //TODO
		}
	}

	@Override
	public IVoiceChannel getVoiceChannel() {
		return currentVoiceChannel;
	}

	@Override
	public void setVoiceChannel(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("Voice channel cannot be null!");
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
		EventLoop eventLoop = player.getChannel().eventLoop();
		if(eventLoop.inEventLoop()) {
			switchChannels(oldChannel, channel);
		}else {
			eventLoop.execute(() -> {
				switchChannels(oldChannel, channel);
			});
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
		removeFromChannel(oldChannel);
	}

	private void switchChannels(IVoiceChannel oldChannel, IVoiceChannel newChannel) {
		if(oldChannel != DisabledChannel.INSTANCE) {
			removeFromChannel(oldChannel);
		}else {
			enableVoice();
		}
		if(newChannel != DisabledChannel.INSTANCE) {
			addToChannel(newChannel);
		}else {
			disableVoice();
		}
	}

	private void enableVoice() {
		if(!isServerEnable) {
			isServerEnable = true;
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(true, voice.getICEServers()));
		}
	}

	private void disableVoice() {
		if(isServerEnable) {
			isServerEnable = false;
			player.sendEaglerMessage(new SPacketVoiceSignalAllowedEAG(false, null));
		}
	}

	private void addToChannel(IVoiceChannel channel) {
		//TODO
	}

	private void removeFromChannel(IVoiceChannel channel) {
		//TODO
	}

}
