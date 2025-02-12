package net.lax1dude.eaglercraft.backend.server.api.event;

public interface ICancellableEvent {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

}
