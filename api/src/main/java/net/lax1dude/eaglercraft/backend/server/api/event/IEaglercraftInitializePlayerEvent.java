package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.Map;

import javax.annotation.Nonnull;

public interface IEaglercraftInitializePlayerEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	@Nonnull
	Map<String, byte[]> getExtraProfileData();

}
