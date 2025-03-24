package net.lax1dude.eaglercraft.backend.server.api.event;

import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceManagerImpl;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceServiceImpl;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;

public interface IEaglercraftVoiceChangeEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	default IVoiceManagerImpl<PlayerObject> getVoiceManager() {
		return getPlayer().getVoiceManager();
	}

	default IVoiceServiceImpl<PlayerObject> getVoiceService() {
		return getServerAPI().getVoiceService();
	}

	EnumVoiceState getVoiceStateOld();

	IVoiceChannel getVoiceChannelOld();

	EnumVoiceState getVoiceStateNew();

	IVoiceChannel getVoiceChannelNew();

}
