package net.lax1dude.eaglercraft.backend.server.bungee.event;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bungee.event.EaglercraftWebViewMessageEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

class BungeeWebViewMessageEventImpl extends EaglercraftWebViewMessageEvent {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final String channel;
	private final EnumMessageType type;
	private final byte[] data;
	private String asString;

	BungeeWebViewMessageEventImpl(IEaglerXServerAPI<ProxiedPlayer> api, IEaglerPlayer<ProxiedPlayer> player,
			String channel, EnumMessageType type, byte[] data) {
		this.api = api;
		this.player = player;
		this.channel = channel;
		this.type = type;
		this.data = data;
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
	public String getChannel() {
		return channel;
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
