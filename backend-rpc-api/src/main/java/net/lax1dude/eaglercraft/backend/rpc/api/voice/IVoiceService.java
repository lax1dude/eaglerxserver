package net.lax1dude.eaglercraft.backend.rpc.api.voice;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerPlayer;
import net.lax1dude.eaglercraft.backend.rpc.api.IEaglerXBackendRPC;

public interface IVoiceService<PlayerObject> {

	@Nonnull
	IEaglerXBackendRPC<PlayerObject> getServerAPI();

	@Nullable
	default IVoiceManager<PlayerObject> getVoiceManager(@Nonnull PlayerObject player) {
		IEaglerPlayer<PlayerObject> eagPlayer = getServerAPI().getEaglerPlayer(player);
		return eagPlayer != null ? eagPlayer.getVoiceManager() : null;
	}

	boolean isVoiceEnabled();

	@Nonnull
	Collection<ICEServerEntry> getICEServers();

	void setICEServers(@Nonnull Collection<ICEServerEntry> servers);

	boolean getOverrideICEServers();

	void setOverrideICEServers(boolean enable);

	@Nonnull
	IVoiceChannel createVoiceChannel();

	@Nonnull
	IVoiceChannel getGlobalVoiceChannel();

	@Nonnull
	IVoiceChannel getDisabledVoiceChannel();

	@Nonnull
	Collection<IEaglerPlayer<PlayerObject>> getConnectedPlayers(@Nonnull IVoiceChannel channel);

}
