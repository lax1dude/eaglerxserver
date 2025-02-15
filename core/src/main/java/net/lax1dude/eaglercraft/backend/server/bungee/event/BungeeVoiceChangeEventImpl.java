package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.lax1dude.eaglercraft.backend.server.api.voice.IVoiceChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeVoiceChangeEventImpl extends EaglercraftVoiceChangeEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final EnumVoiceState voiceStateOld;
	private final IVoiceChannel voiceChannelOld;
	private final EnumVoiceState voiceStateNew;
	private final IVoiceChannel voiceChannelNew;

	BungeeVoiceChangeEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player,
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
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
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
