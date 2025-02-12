package net.lax1dude.eaglercraft.eaglerxserver.api.voice;

import net.lax1dude.eaglercraft.eaglerxserver.api.players.IEaglerPlayer;

public interface IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceService<PlayerObject> getVoiceService();

	boolean isBackendRelayMode();

	EnumVoiceState getVoiceState();

	IVoiceChannel getVoiceChannel();

	void setVoiceChannel(IVoiceChannel channel);

}
