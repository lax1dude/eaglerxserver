package net.lax1dude.eaglercraft.backend.server.api.voice;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;

public interface IVoiceService<PlayerObject> {

	@Nonnull
	IEaglerXServerAPI<PlayerObject> getServerAPI();

	@Nullable
	default IVoiceManager<PlayerObject> getVoiceManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	boolean isVoiceEnabled();

	boolean isVoiceEnabledAllServers();

	boolean isVoiceEnabledOnServer(@Nonnull String serverName);

	boolean isSeparateServerChannels();

	@Nullable
	IVoiceChannel getServerVoiceChannel(@Nonnull String serverName);

	boolean isBackendRelayMode();

	@Nonnull
	Collection<ICEServerEntry> getICEServers();

	void setICEServers(@Nonnull Collection<ICEServerEntry> servers);

	@Nonnull
	IVoiceChannel createVoiceChannel();

	@Nonnull
	IVoiceChannel getGlobalVoiceChannel();

	@Nonnull
	IVoiceChannel getDisabledVoiceChannel();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(@Nonnull IVoiceChannel channel);

}
