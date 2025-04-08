package net.lax1dude.eaglercraft.backend.rpc.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event.EaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.EnumVoiceState;

class BukkitVoiceChangeEventImpl extends EaglercraftVoiceChangeEvent {

	private final IEaglerXBackendRPC<Player> api;
	private final IEaglerPlayer<Player> player;
	private final EnumVoiceState stateOld;
	private final EnumVoiceState stateNew;

	BukkitVoiceChangeEventImpl(IEaglerXBackendRPC<Player> api, IEaglerPlayer<Player> player,
			EnumVoiceState stateOld, EnumVoiceState stateNew) {
		this.api = api;
		this.player = player;
		this.stateOld = stateOld;
		this.stateNew = stateNew;
	}

	@Override
	public IEaglerXBackendRPC<Player> getServerAPI() {
		return api;
	}

	@Override
	public IEaglerPlayer<Player> getPlayer() {
		return player;
	}

	@Override
	public EnumVoiceState getVoiceStateOld() {
		return stateOld;
	}

	@Override
	public EnumVoiceState getVoiceStateNew() {
		return stateNew;
	}

}
