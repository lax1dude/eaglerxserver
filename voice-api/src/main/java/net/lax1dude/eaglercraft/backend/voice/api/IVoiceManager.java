package net.lax1dude.eaglercraft.backend.voice.api;

public interface IVoiceManager<PlayerObject> {

	IVoicePlayer<PlayerObject> getPlayer();

	IVoiceService<PlayerObject> getVoiceService();

	EnumVoiceState getVoiceState();

	IVoiceChannel getVoiceChannel();

	void setVoiceChannel(IVoiceChannel channel);

	boolean isServerManaged();

	void setServerManaged(boolean managed);

}
