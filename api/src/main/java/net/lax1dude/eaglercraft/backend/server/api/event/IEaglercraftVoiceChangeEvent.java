package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManager;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceService;

public interface IEaglercraftVoiceChangeEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	@Nonnull
	default IVoiceManager<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	@Nonnull
	default IVoiceService<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	@Nonnull
	EnumVoiceState getVoiceStateOld();

	@Nonnull
	EnumVoiceState getVoiceStateNew();

}
