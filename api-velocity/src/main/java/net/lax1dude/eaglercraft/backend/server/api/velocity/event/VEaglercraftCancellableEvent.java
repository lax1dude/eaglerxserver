package net.lax1dude.eaglercraft.backend.server.api.velocity.event;

import com.velocitypowered.api.proxy.Player;

import net.lax1dude.eaglercraft.backend.server.api.IEaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.event.ICancellableEvent;

class VEaglercraftCancellableEvent extends VEaglercraftBaseEvent implements ICancellableEvent {

	private boolean cancelled;

	VEaglercraftCancellableEvent(IEaglerXServerAPI<Player> api) {
		super(api);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
