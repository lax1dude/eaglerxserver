package net.lax1dude.eaglercraft.backend.rpc.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceService;

public interface IEaglercraftVoiceCapableEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	@Nonnull
	default IVoiceManager<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	@Nonnull
	default IVoiceService<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	@Nonnull
	IVoiceChannel getTargetChannel();

	@Nonnull
	void setTargetChannel(IVoiceChannel channel);

}
