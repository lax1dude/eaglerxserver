package net.lax1dude.eaglercraft.backend.server.bukkit.event;

import java.nio.charset.StandardCharsets;

import org.bukkit.entity.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.event.EaglercraftWebViewMessageEvent;

class BukkitWebViewMessageEventImpl extends EaglercraftWebViewMessageEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final EnumMessageType type;
	private final byte[] data;
	private String asString;

	BukkitWebViewMessageEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			EnumMessageType type, byte[] data) {
		this.api = api;
		this.player = player;
		this.type = type;
		this.data = data;
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
	public EnumMessageType getType() {
		return type;
	}

	@Override
	public String getAsString() {
		if(asString == null) {
			asString = new String(data, StandardCharsets.UTF_8);
		}
		return asString;
	}

	@Override
	public byte[] getAsBinary() {
		return data;
	}

}
