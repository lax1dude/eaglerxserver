package net.lax1dude.eaglercraft.backend.server.api.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.lax1dude.eaglercraft.backend.server.api.event.IEaglercraftRevokeSessionQueryEvent;

public abstract class EaglercraftRevokeSessionQueryEvent extends Event
		implements IEaglercraftRevokeSessionQueryEvent<Player> {

	private static final HandlerList handlers = new HandlerList();

	protected EaglercraftRevokeSessionQueryEvent() {
		super(true);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
