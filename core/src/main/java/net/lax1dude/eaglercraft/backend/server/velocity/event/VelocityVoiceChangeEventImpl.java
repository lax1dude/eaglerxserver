package net.lax1dude.eaglercraft.backend.server.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.voice.api.IVoiceChannel;

class VelocityVoiceChangeEventImpl extends EaglercraftVoiceChangeEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final EnumVoiceState voiceStateOld;
	private final IVoiceChannel voiceChannelOld;
	private final EnumVoiceState voiceStateNew;
	private final IVoiceChannel voiceChannelNew;

	VelocityVoiceChangeEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			EnumVoiceState voiceStateOld, IVoiceChannel voiceChannelOld, EnumVoiceState voiceStateNew,
			IVoiceChannel voiceChannelNew) {
		this.api = api;
		this.player = player;
		this.voiceStateOld = voiceStateOld;
		this.voiceChannelOld = voiceChannelOld;
		this.voiceStateNew = voiceStateNew;
		this.voiceChannelNew = voiceChannelNew;
	}

	@Override
	public IEaglerXServerAPI<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<Player> getPlayer() {
		return player;
	}

	@Override
	public EnumVoiceState getVoiceStateOld() {
		return voiceStateOld;
	}

	@Override
	public IVoiceChannel getVoiceChannelOld() {
		return voiceChannelOld;
	}

	@Override
	public EnumVoiceState getVoiceStateNew() {
		return voiceStateNew;
	}

	@Override
	public IVoiceChannel getVoiceChannelNew() {
		return voiceChannelNew;
	}

}
