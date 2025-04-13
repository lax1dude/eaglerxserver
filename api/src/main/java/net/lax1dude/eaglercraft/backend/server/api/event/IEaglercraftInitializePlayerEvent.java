package net.lax1dude.eaglercraft.backend.server.api.event;

import java.util.Map;

public interface IEaglercraftInitializePlayerEvent<PlayerObject> extends IBasePlayerEvent<PlayerObject> {

	Map<String, byte[]> getExtraProfileData();

}
