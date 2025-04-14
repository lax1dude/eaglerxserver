package net.lax1dude.eaglercraft.backend.rpc.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;
import net.lax1dude.eaglercraft.backend.rpc.api.bukkit.event.EaglercraftVoiceCapableEvent;
import net.lax1dude.eaglercraft.backend.rpc.api.voice.IVoiceChannel;

class BukkitVoiceCapableEventImpl extends EaglercraftVoiceCapableEvent {

	private final IEaglerXBackendRPC<Player> api;
	private final IEaglerPlayer<Player> player;
	private IVoiceChannel channel;

	BukkitVoiceCapableEventImpl(IEaglerXBackendRPC<Player> api, IEaglerPlayer<Player> player,
			IVoiceChannel channel) {
		this.api = api;
		this.player = player;
		this.channel = channel;
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
	public IVoiceChannel getTargetChannel() {
		return channel;
	}

	@Override
	public void setTargetChannel(IVoiceChannel channel) {
		if(channel == null) {
			throw new NullPointerException("channel");
		}
		this.channel = channel;
	}

}
