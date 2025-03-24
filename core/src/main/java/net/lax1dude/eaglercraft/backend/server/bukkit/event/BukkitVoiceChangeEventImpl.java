package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.voice.api.EnumVoiceState;

class BukkitVoiceChangeEventImpl extends EaglercraftVoiceChangeEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final EnumVoiceState voiceStateOld;
	private final EnumVoiceState voiceStateNew;

	BukkitVoiceChangeEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			EnumVoiceState voiceStateOld, EnumVoiceState voiceStateNew) {
		this.api = api;
		this.player = player;
		this.voiceStateOld = voiceStateOld;
		this.voiceStateNew = voiceStateNew;
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
	public EnumVoiceState getVoiceStateNew() {
		return voiceStateNew;
	}

}
