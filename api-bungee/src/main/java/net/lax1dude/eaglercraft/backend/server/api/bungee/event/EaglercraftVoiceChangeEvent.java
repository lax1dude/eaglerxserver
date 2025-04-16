package net.lax1dude.eaglercraft.backend.server.api.bungee.event;

import javax.annotation.Nonnull;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftVoiceChangeEvent;
import net.lax1dude.eaglercraft.backend.server.api.voice.EnumVoiceState;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public final class EaglercraftVoiceChangeEvent extends Event implements IEaglercraftVoiceChangeEvent<ProxiedPlayer> {

	private final IEaglerXServerAPI<ProxiedPlayer> api;
	private final IEaglerPlayer<ProxiedPlayer> player;
	private final EnumVoiceState voiceStateOld;
	private final EnumVoiceState voiceStateNew;

	public EaglercraftVoiceChangeEvent(@Nonnull IEaglerXServerAPI<ProxiedPlayer> api,
			@Nonnull IEaglerPlayer<ProxiedPlayer> player, @Nonnull EnumVoiceState voiceStateOld,
			@Nonnull EnumVoiceState voiceStateNew) {
		this.api = api;
		this.player = player;
		this.voiceStateOld = voiceStateOld;
		this.voiceStateNew = voiceStateNew;
	}

	@Nonnull
	@Override
	public IEaglerXServerAPI<ProxiedPlayer> getServerAPI() {
		return api;
	}

	@Nonnull
	@Override
	public IEaglerPlayer<ProxiedPlayer> getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public EnumVoiceState getVoiceStateOld() {
		return voiceStateOld;
	}

	@Nonnull
	@Override
	public EnumVoiceState getVoiceStateNew() {
		return voiceStateNew;
	}

}
