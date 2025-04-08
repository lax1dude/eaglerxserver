package net.lax1dude.eaglercraft.backend.server.bungee.event;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeVoiceChangeEventImpl extends EaglercraftVoiceChangeEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final EnumVoiceState voiceStateOld;
	private final EnumVoiceState voiceStateNew;

	BungeeVoiceChangeEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player,
			EnumVoiceState voiceStateOld, EnumVoiceState voiceStateNew) {
		this.api = api;
		this.player = player;
		this.voiceStateOld = voiceStateOld;
		this.voiceStateNew = voiceStateNew;
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
	public EnumVoiceState getVoiceStateNew() {
		return voiceStateNew;
	}

}
