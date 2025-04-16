package net.lax1dude.eaglercraft.backend.server.api.voice;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;

public interface IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceService<PlayerObject> getVoiceService();

	EnumVoiceState getVoiceState();

	IVoiceChannel getVoiceChannel();

	void setVoiceChannel(IVoiceChannel channel);

	default void setVoiceDisabled() {
		setVoiceChannel(getVoiceService().getDisabledVoiceChannel());
	}

	boolean isServerManaged();

	void setServerManaged(boolean managed);

	boolean isBackendRelayMode();

}
