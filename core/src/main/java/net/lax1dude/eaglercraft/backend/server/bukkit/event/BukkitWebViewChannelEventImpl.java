package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewChannelEvent;

class BukkitWebViewChannelEventImpl extends EaglercraftWebViewChannelEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final EnumEventType type;
	private final String channel;

	BukkitWebViewChannelEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			EnumEventType type, String channel) {
		this.api = api;
		this.player = player;
		this.type = type;
		this.channel = channel;
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
	public EnumEventType getType() {
		return type;
	}

	@Override
	public String getChannel() {
		return channel;
	}

}
