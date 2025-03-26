package net.lax1dude.eaglercraft.backend.server.velocity.event;

import java.nio.charset.StandardCharsets;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.velocity.event.EaglercraftWebViewMessageEvent;

class VelocityWebViewMessageEventImpl extends EaglercraftWebViewMessageEvent {

	private final IEaglerXServerAPI<Player> api;
	private final IEaglerPlayer<Player> player;
	private final String channel;
	private final EnumMessageType type;
	private final byte[] data;
	private String asString;

	VelocityWebViewMessageEventImpl(IEaglerXServerAPI<Player> api, IEaglerPlayer<Player> player,
			String channel, EnumMessageType type, byte[] data) {
		this.api = api;
		this.player = player;
		this.channel = channel;
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
