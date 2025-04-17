package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;

public interface IVoiceManager<PlayerObject> {

	@Nonnull
	IEaglerPlayer<PlayerObject> getPlayer();

	@Nonnull
	IVoiceService<PlayerObject> getVoiceService();

	@Nonnull
	EnumVoiceState getVoiceState();

	@Nonnull
	IVoiceChannel getVoiceChannel();

	void setVoiceChannel(@Nonnull IVoiceChannel channel);

	default void setVoiceDisabled() {
		setVoiceChannel(getVoiceService().getDisabledVoiceChannel());
	}

}
