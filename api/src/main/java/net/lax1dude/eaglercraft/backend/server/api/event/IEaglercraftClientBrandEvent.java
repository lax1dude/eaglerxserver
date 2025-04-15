package net.lax1dude.eaglercraft.backend.server.api.event;

import javax.annotation.Nullable;

public interface IEaglercraftClientBrandEvent<PlayerObject, ComponentObject>
		extends IBaseHandshakeEvent<PlayerObject>, ICancellableEvent {

	@Nullable
	ComponentObject getMessage();

	void setMessage(@Nullable String message);

	void setMessage(@Nullable ComponentObject message);

	default void setKickMessage(@Nullable String message) {
		setCancelled(true);
		setMessage(message);
	}

	default void setKickMessage(@Nullable ComponentObject message) {
		setCancelled(true);
		setMessage(message);
	}

}
