package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;

public interface IVoiceManager<PlayerObject> {

	IEaglerPlayer<PlayerObject> getPlayer();

	IVoiceService<PlayerObject> getVoiceService();

	EnumVoiceState getVoiceState();

	IVoiceChannel getVoiceChannel();

	void setVoiceChannel(IVoiceChannel channel);

}
