package net.lax1dude.eaglercraft.backend.server.api.event;

public interface IEaglercraftClientBrandEvent<PlayerObject, ComponentObject> extends IBaseHandshakeEvent<PlayerObject>, ICancellableEvent {

	ComponentObject getMessage();

	void setMessage(String message);

	void setMessage(ComponentObject message);

	default void setKickMessage(String message) {
		setCancelled(true);
		setMessage(message);
	}

	default void setKickMessage(ComponentObject message) {
		setCancelled(true);
		setMessage(message);
	}

}
